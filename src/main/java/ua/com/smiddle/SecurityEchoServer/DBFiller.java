package ua.com.smiddle.SecurityEchoServer;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMethod;
import ua.com.smiddle.SecurityEchoServer.config.AppConfig;
import ua.com.smiddle.common.model.Access;
import ua.com.smiddle.common.model.*;
import ua.com.smiddle.logger.produser.LogProducerImpl;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Added by A.Osadchuk on 29.03.2016 at 16:08.
 * Project: Manager
 */
@SuppressWarnings("unchecked")
public class DBFiller {
    public static void main(String[] args) {
        checkDB();
    }

    public static void checkDB() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Smiddle");
        EntityManager em = emf.createEntityManager();
        Query query;

        // ================ ADM_GROUPS ================
        Group group = null;
        try {
            query = em.createQuery("SELECT g FROM Group g WHERE g.name=:name", Group.class).setParameter("name", "ROOT");
            em.getTransaction().begin();
            group = (Group) query.getSingleResult();
            em.getTransaction().commit();
        } catch (NoResultException e) {
            group = new Group();
            group.setName("ROOT");
            group = em.merge(group);
            em.getTransaction().commit();
        } catch (Exception e) {
            LogProducerImpl.logStdOut("DB_Filler", "ADM_GROUPS: throws Exception=" + e.getMessage());
            em.getTransaction().rollback();
        }

        // =============== ADM_ROLES =================
        Role role = null;
        try {
            query = em.createQuery("SELECT r FROM Role r WHERE r.name=:name", Role.class).setParameter("name", "ROOT");
            em.getTransaction().begin();
            role = (Role) query.getSingleResult();
            em.getTransaction().commit();
        } catch (NoResultException e) {
            role = new Role();
            role.setName("ROOT");
            role.setSystem(true);
            role = em.merge(role);
            em.getTransaction().commit();
        } catch (Exception e) {
            LogProducerImpl.logStdOut("DB_Filler", "ADM_ROLES: throws Exception=" + e.getMessage());
            em.getTransaction().rollback();
        }

        // =============== ADM_MENU ================
        fillMenu(em);
        // ================ ADM_USER ==================
        try {
            query = em.createQuery("SELECT u FROM User u WHERE u.login=:login", User.class).setParameter("login", "root");
            em.getTransaction().begin();
            query.getSingleResult();
            em.getTransaction().commit();
        } catch (NoResultException e) {
            PasswordEncoder encoder = new BCryptPasswordEncoder(10);
            User user = new User();
            user.setAgentId("1");
            user.setLogin("root");
            user.setPassword(encoder.encode("root"));
            user.setFname("Root");
            user.setPname("User");
            user.setLname("Initiate");
            user.setEmail("E-mail");
            user.setPhone("(080) 600-300-20");
            user.setFax("(044) 232-22-22");
            user.setLocale("UA");
            user.setDateCreate(new Date());
            user.setEnabled(true);
            user.setDeleted(false);
            user.getGroups().add(group);
            user.getRoles().add(role);
            em.persist(user);
            em.getTransaction().commit();
        } catch (Exception e) {
            LogProducerImpl.logStdOut("DB_Filler", "ADM_USER: throws Exception=" + e.getMessage());
            em.getTransaction().rollback();
        }
        // ================ SPECIAL ROLES ==================
        checkSpecialRoles(em);
        // ================= CLOSING ==================
        em.close();
        emf.close();
    }

    private static void fillMenu(EntityManager em) {
        LogProducerImpl.logStdOut("DB_Filler", "FILL MENU proceed...");
        int sortOrder = 0;
        Query taskQuery = em.createQuery("SELECT t FROM TaskURL t WHERE t.code=:code", TaskURL.class);
        Collection<AuthorizationType> types = em
                .createQuery("SELECT t FROM AuthorizationType t WHERE t.type='SMIDDLE_TOKEN'", AuthorizationType.class)
                .getResultList();
        try {
            try {
                taskQuery.setParameter("code", "ses_test_anonymous");
                taskQuery.getSingleResult();
            } catch (NoResultException e) {
                em.getTransaction().begin();
                TaskURL task = new TaskURL("/ses/test/anonymous", "anonymous and principal", "ses_test_anonymous", AppConfig.PROJECT, RequestMethod.POST,Access.ANONYMOUS);
                task.setAuthorizationTypes(types);
                em.merge(task);
                em.getTransaction().commit();
            }

            try {
                taskQuery.setParameter("code", "ses_test_principal_all");
                taskQuery.getSingleResult();
            } catch (NoResultException e) {
                em.getTransaction().begin();
                TaskURL task = new TaskURL("/ses/test/principal_all", "anonymous and principal", "ses_test_principal_all", AppConfig.PROJECT);
                task.setAuthorizationTypes(types);
                em.merge(task);
                em.getTransaction().commit();
            }
            try {
                taskQuery.setParameter("code", "ses_test_principal_post");
                taskQuery.getSingleResult();
            } catch (NoResultException e) {
                em.getTransaction().begin();
                TaskURL task = new TaskURL("/ses/test/principal_post", "anonymous and principal", "ses_test_principal_post", AppConfig.PROJECT,RequestMethod.POST,Access.PRINCIPAL);
                task.setAuthorizationTypes(types);
                em.merge(task);
                em.getTransaction().commit();
            }
            try {
                taskQuery.setParameter("code", "ses_test_principal_post");
                taskQuery.getSingleResult();
            } catch (NoResultException e) {
                em.getTransaction().begin();
                TaskURL task = new TaskURL("/ses/test/matcher/**", "anonymous and principal", "ses_test_principal_post", AppConfig.PROJECT,RequestMethod.POST,Access.PRINCIPAL);
                task.setAuthorizationTypes(types);
                em.merge(task);
                em.getTransaction().commit();
            }
            LogProducerImpl.logStdOut("DB_Filler", "FILL MENU finished!!!");
        } catch (Exception e) {
            LogProducerImpl.logStdOut("DB_Filler", "FILL MENU THROWS EXCEPTION=" + e.getMessage());
            em.getTransaction().rollback();
        }
    }

    private static void checkSpecialRoles(EntityManager em) {
        LogProducerImpl.logStdOut("DB_Filler", "checkSpecialRoles: started...");
        Query query = em.createQuery("SELECT r FROM Role r WHERE r.name=:code", Role.class);
        Role role;
        String[] roles = {"ROOT", "ADMINISTRATOR", "SUPERVISOR", "USER"};
        for (String roleName : roles) {
            try {
                em.getTransaction().begin();
                query.setParameter("code", roleName);
                role = (Role) query.getSingleResult();
                em.getTransaction().commit();
                if (roleName.equals("ROOT"))
                    checkRoleFullAccess(em, role);
            } catch (NoResultException e) {
                role = new Role();
                role.setName(roleName);
                role.setSystem(true);
                role = em.merge(role);
                em.getTransaction().commit();
            } catch (Exception e) {
                LogProducerImpl.logStdOut("DB_Filler", "checkSpecialRoles: throw EXCEPTION=" + e.getMessage());
                em.getTransaction().rollback();
            }
        }
        LogProducerImpl.logStdOut("DB_Filler", "checkSpecialRoles: done");
    }

    private static void checkRoleFullAccess(EntityManager em, Role role) {
        LogProducerImpl.logStdOut("DB_Filler", "checkRoleFullAccess: started for role=" + role.getName());
        try {
            em.getTransaction().begin();
            Query query = em.createQuery("SELECT t from TaskURL t where t.access=:access", TaskURL.class);
            query.setParameter("access", Access.PRINCIPAL);
            List<TaskURL> tasks = query.getResultList();
            em.getTransaction().commit();
            if (!tasks.isEmpty()) {
                em.getTransaction().begin();
                for (TaskURL task : tasks) {
                    TaskURL tmp = em.getReference(TaskURL.class, task.getId());
                    List<Role> rl = tmp.getRoleList();
                    if (rl == null)
                        rl = new ArrayList<>();
                    if (!rl.contains(role)) {
                        rl.add(role);
                        task.setRoleList(rl);
                        em.merge(task);
                        LogProducerImpl.logStdOut("DB_Filler", "checkRoleFullAccess: updated for role=" + role.getName() + " code=" + task.getCode());
                    }
                }
                em.getTransaction().commit();
            }
        } catch (Exception e) {
            LogProducerImpl.logStdOut("DB_Filler", "checkRoleFullAccess: throws Exception=" + e.getMessage());
            em.getTransaction().rollback();
        }
        LogProducerImpl.logStdOut("DB_Filler", "checkRoleFullAccess: checking for role=" + role.getName() + " done.");
    }
}
