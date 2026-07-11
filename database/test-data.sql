insert into app_users (id, username, email, password_hash, created_at) values
    (1, 'mathilde', 'mathilde@example.com', 'pbkdf2$65536$bWF0aGlsZGUtc2FsdC0wMQ==$sTqyyziuNnajCV+sOb7lPxtb6peZoatp1yXujbrVx5o=', now()),
    (2, 'nicolas', 'nicolas@example.com', 'pbkdf2$65536$bmljb2xhcy1zYWx0LTAx$QWX78xh/aE+erfY7ZZe6MNuyOMNR41emksuBWb+g+ZI=', now()),
    (3, 'mariana', 'mariana@example.com', 'pbkdf2$65536$bWFyaWFuYS1zYWx0LTAx$kfh7kXfsdyUCELKFbXf2FiKeuXhCd9+2X/2d3Pe70DU=', now());

insert into projects (id, name, description, start_date, created_at) values
    (1, 'ProjectManagementTool', 'Plateforme de gestion de projet collaboratif pour équipes de développement logiciel.', '2026-07-01', now()),
    (2, 'Refonte portail client', 'Modernisation du portail client et suivi des demandes support.', '2026-06-15', now());

insert into project_members (id, project_id, user_id, role, joined_at) values
    (1, 1, 1, 'ADMIN', now()),
    (2, 1, 2, 'MEMBER', now()),
    (3, 1, 3, 'OBSERVER', now()),
    (4, 2, 1, 'ADMIN', now()),
    (5, 2, 3, 'MEMBER', now());

insert into tasks (id, project_id, title, description, due_date, priority, status, created_by, created_at, updated_at) values
    (1, 1, 'Modéliser le domaine métier', 'Créer les entités utilisateurs, projets, rôles, tâches, notifications et historique.', '2026-07-10', 'URGENT', 'IN_PROGRESS', 1, now(), now()),
    (2, 1, 'Construire le tableau Kanban', 'Afficher les tâches par statut et permettre les changements de statut.', '2026-07-12', 'HIGH', 'TODO', 1, now(), now()),
    (3, 1, 'Configurer la pipeline CI/CD', 'Automatiser tests, build Docker et push Docker Hub.', '2026-07-15', 'MEDIUM', 'TODO', 2, now(), now()),
    (4, 2, 'Audit UX du portail', 'Lister les parcours à simplifier pour les utilisateurs support.', '2026-07-20', 'LOW', 'REVIEW', 3, now(), now());

insert into task_assignments (id, task_id, assignee_id, assigned_by, assigned_at) values
    (1, 1, 2, 1, now()),
    (2, 2, 1, 1, now()),
    (3, 3, 2, 1, now()),
    (4, 4, 3, 1, now());

insert into notifications (id, recipient_id, project_id, task_id, title, message, created_at) values
    (1, 2, 1, 1, 'Task assigned', 'mathilde assigned you to Modéliser le domaine métier', now()),
    (2, 1, 1, 2, 'Task assigned', 'mathilde assigned you to Construire le tableau Kanban', now());

insert into task_activities (id, task_id, actor_id, action, details, created_at) values
    (1, 1, 1, 'CREATED', 'Task created', now()),
    (2, 1, 1, 'ASSIGNED', 'Assigned to nicolas', now()),
    (3, 1, 2, 'STATUS_CHANGED', 'Updated status', now()),
    (4, 2, 1, 'CREATED', 'Task created', now());

select setval('app_users_id_seq', 3, true);
select setval('projects_id_seq', 2, true);
select setval('project_members_id_seq', 5, true);
select setval('tasks_id_seq', 4, true);
select setval('task_assignments_id_seq', 4, true);
select setval('notifications_id_seq', 2, true);
select setval('task_activities_id_seq', 4, true);
