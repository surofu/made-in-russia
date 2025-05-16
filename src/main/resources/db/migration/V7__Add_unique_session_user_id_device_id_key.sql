DO
$$
    BEGIN
        IF NOT EXISTS (SELECT 1
                       FROM information_schema.table_constraints
                       WHERE table_name = 'sessions'
                         AND constraint_name = 'uk_session_user_device'
                         AND constraint_type = 'UNIQUE') THEN
            EXECUTE 'ALTER TABLE sessions ADD CONSTRAINT uk_session_user_device UNIQUE (user_id, device_id)';
        END IF;
    END
$$;