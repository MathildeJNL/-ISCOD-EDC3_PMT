create table app_users (
    id bigserial primary key,
    username varchar(80) not null,
    email varchar(160) not null,
    password_hash varchar(255) not null,
    created_at timestamptz not null default now(),
    constraint uk_app_users_email unique (email),
    constraint uk_app_users_username unique (username)
);

create table projects (
    id bigserial primary key,
    name varchar(120) not null,
    description varchar(1200) not null,
    start_date date not null,
    created_at timestamptz not null default now()
);

create table project_members (
    id bigserial primary key,
    project_id bigint not null references projects(id) on delete cascade,
    user_id bigint not null references app_users(id) on delete cascade,
    role varchar(20) not null,
    joined_at timestamptz not null default now(),
    constraint uk_project_members_project_user unique (project_id, user_id),
    constraint ck_project_members_role check (role in ('ADMIN', 'MEMBER', 'OBSERVER'))
);

create table tasks (
    id bigserial primary key,
    project_id bigint not null references projects(id) on delete cascade,
    title varchar(160) not null,
    description varchar(2000) not null,
    due_date date not null,
    priority varchar(20) not null,
    status varchar(20) not null default 'TODO',
    created_by bigint not null references app_users(id),
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    constraint ck_tasks_priority check (priority in ('LOW', 'MEDIUM', 'HIGH', 'URGENT')),
    constraint ck_tasks_status check (status in ('TODO', 'IN_PROGRESS', 'REVIEW', 'DONE'))
);

create table task_assignments (
    id bigserial primary key,
    task_id bigint not null references tasks(id) on delete cascade,
    assignee_id bigint not null references app_users(id) on delete cascade,
    assigned_by bigint not null references app_users(id),
    assigned_at timestamptz not null default now(),
    constraint uk_task_assignments_task_user unique (task_id, assignee_id)
);

create table notifications (
    id bigserial primary key,
    recipient_id bigint not null references app_users(id) on delete cascade,
    project_id bigint not null references projects(id) on delete cascade,
    task_id bigint references tasks(id) on delete cascade,
    title varchar(160) not null,
    message varchar(1200) not null,
    created_at timestamptz not null default now(),
    read_at timestamptz
);

create table task_activities (
    id bigserial primary key,
    task_id bigint not null references tasks(id) on delete cascade,
    actor_id bigint not null references app_users(id),
    action varchar(30) not null,
    details varchar(1000) not null,
    created_at timestamptz not null default now(),
    constraint ck_task_activities_action check (
        action in ('CREATED', 'UPDATED', 'STATUS_CHANGED', 'ASSIGNED', 'COMMENTED')
    )
);

create index idx_project_members_user on project_members(user_id);
create index idx_tasks_project_status on tasks(project_id, status);
create index idx_task_assignments_assignee on task_assignments(assignee_id);
create index idx_notifications_recipient on notifications(recipient_id, created_at desc);
create index idx_task_activities_task on task_activities(task_id, created_at desc);
