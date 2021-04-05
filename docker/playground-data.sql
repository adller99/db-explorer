INSERT INTO playground.customer (id, first_name, last_name, registered_at, created)
VALUES (1, 'Karel', 'Vomackojz', '2021-03-04', now());

INSERT INTO playground.customer_details (customer_id, email, phone_number, is_premium)
VALUES (1, 'karel@vomackojz.cz', '+420 777 222 666', true);

INSERT INTO playground.statistics (sold_premium_count)
VALUES (10), (5), (7), (8), (8), (9), (3), (10), (10), (11), (12), (15);
