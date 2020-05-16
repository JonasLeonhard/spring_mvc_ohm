--- Create Admin & User Role
INSERT INTO public.role
    (id, name)
VALUES (1, 'Admin')
ON CONFLICT (id)
    DO NOTHING;

INSERT INTO public.role
    (id, name)
VALUES (2, 'User')
ON CONFLICT (id)
    DO NOTHING;

-- --- Create Test User with pass == username
-- INSERT INTO public.user_profile
-- (id, "name", "password", username)
-- VALUES(1, 'Testname', '$2y$12$aft6P5zfCkLE1OBgP1vbieUll/of/PNc0/PdHQABTBTkuFLc9BBG2', 'Testusername')
-- ON CONFLICT (id)
-- DO NOTHING;
--
-- --- Create inital Mapping
-- INSERT INTO public.user_profile_roles
-- (users_id, roles_id)
-- VALUES(1, 1)
-- ON CONFLICT (users_id, roles_id)
-- DO NOTHING;
