DROP FUNCTION IF EXISTS bpmn.generate_data();
CREATE OR REPLACE FUNCTION bpmn.generate_data()
RETURNS boolean as $$
DECLARE
    number_of_parkings integer;
    parking_letters integer;
    spots_per_letter integer;
    spot_name character varying;
	spot_let character[];
    parking_index integer;
BEGIN

RAISE NOTICE 'CLEAR ALL DATA';
DELETE FROM bpmn.spot;
DELETE FROM bpmn.parking;
DELETE FROM bpmn.spot_type;

ALTER SEQUENCE bpmn.parking_id_seq RESTART WITH 1;
ALTER SEQUENCE bpmn.spot_id_seq RESTART WITH 1;
ALTER SEQUENCE bpmn.spot_type_id_seq RESTART WITH 1;

INSERT INTO bpmn.spot_type (type_name) VALUES ('Car');

spot_let := ARRAY['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L'];

RAISE NOTICE 'Insert Parkings';
INSERT INTO bpmn.parking (city, street, address)
VALUES
    ('Tarnów', 'Plac Dworcowy', '2'),
    ('Tarnów', 'Brodzinskiego', '4'),
    ('Kielce', 'Skibowa', '71'),
    ('Gdańsk', 'Jesienna', '124'),
    ('Gdańsk', 'Wita Stwosza', '77'),
    ('Bydgoszcz', 'Wieluńska', '57'),
    ('Rzeszów', 'Wyżynna', '19'),
    ('Warszawa', 'Niepodległości', '47'),
    ('Lublin', 'Karpacka', '92');
number_of_parkings := (SELECT count(*) FROM bpmn.parking);
RAISE NOTICE 'Inserted % parkings', number_of_parkings;

RAISE NOTICE 'Insert Rates';
INSERT INTO bpmn.price_rate (cost_per_standard_hour, min_hours)
VALUES
    (500, 1),
    (375, 2),
    (300, 5),
    (250, 10);

RAISE NOTICE 'Create Spots';
FOR parking_index in 1..number_of_parkings LOOP 
    parking_letters := (random() * 12)::int;
    spots_per_letter := (10 + random() * 20)::int;
    for letter_index in 1..parking_letters LOOP
        for spot_number IN 1..10 LOOP
            spot_name := CONCAT(spot_let[letter_index], spot_number);
            INSERT INTO bpmn.spot (parking_id, space_code, spot_type_id) VALUES (parking_index, spot_name, 1); 
        END LOOP;
    END LOOP;
END LOOP;
RAISE NOTICE 'Inserted % spots', (SELECT count(*) FROM bpmn.spot);
RETURN TRUE;
END
$$ LANGUAGE 'plpgsql';

SELECT bpmn.generate_data();
