CREATE OR REPLACE FUNCTION jsonb_array_contains_id(jsonb_data jsonb, search_id bigint)
    RETURNS boolean AS $$
BEGIN
    RETURN EXISTS (
        SELECT 1
        FROM jsonb_array_elements(jsonb_data) AS element
        WHERE (element->>'id')::bigint = search_id
    );
END;
$$ LANGUAGE plpgsql;