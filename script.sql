-- USER
-- CREATE USER "SportConnectBE" WITH PASSWORD 'SportCBEBECP23MS2';
-- GRANT ALL PRIVILEGES ON DATABASE "SportConnect" TO "SportConnectBE";
-- GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "SportConnectBE";
-- GRANT ALL PRIVILEGES ON SCHEMA public TO "SportConnectBE";
-- SET role "postgres";

-- DB
BEGIN;

DROP TABLE IF EXISTS "categories" CASCADE;
DROP TABLE IF EXISTS "activityParticipants" CASCADE;
DROP TABLE IF EXISTS "user" CASCADE;
DROP TABLE IF EXISTS "activities" CASCADE;
DROP TABLE IF EXISTS "request" CASCADE;
DROP TYPE IF EXISTS gender_user;
DROP TYPE IF EXISTS status_participant;
DROP TYPE IF EXISTS role_user;
DROP SEQUENCE IF EXISTS users_sequence;
DROP SEQUENCE IF EXISTS activities_sequence;
DROP SEQUENCE IF EXISTS categories_sequence;

CREATE SEQUENCE users_sequence START 1;
CREATE SEQUENCE activities_sequence START 1;
CREATE SEQUENCE categories_sequence START 1;

CREATE TABLE IF NOT EXISTS public.activities
(
    "activityId" SERIAL PRIMARY KEY,
    "hostUserId" integer NOT NULL,
    "categoryId" integer NOT NULL,
    title character varying(100) COLLATE pg_catalog."default" NOT NULL,
    description text,
    place character varying(100) NOT NULL,
    "dateTime" timestamp with time zone NOT NULL,
    duration integer NOT NULL,
    "createdAt" timestamp with time zone NOT NULL,
    "updatedAt" timestamp with time zone NOT NULL,
	"noOfMembers" integer
);

CREATE TYPE gender_user AS ENUM ('0', '1', '2', '9');
CREATE TYPE role_user AS ENUM ('admin', 'user');
CREATE TABLE IF NOT EXISTS public."user"
(
    "userId" SERIAL PRIMARY KEY,
    username character varying(20) COLLATE pg_catalog."default" NOT NULL,
	email character varying(40),
	"role" role_user,
    "profilePicture" text COLLATE pg_catalog."default",
    gender gender_user,
    "dateOfBirth" date,
    "phoneNumber" character varying(10),
    "lineId" character varying(24),
    "lastLogin" timestamp with time zone,
    "registrationDate" date NOT NULL
);

COMMENT ON TABLE public."user"
    IS 'ผู้ใช้';


CREATE TYPE status_participant AS ENUM ('arrived', 'to_be_late', 'ready', 'not_coming');

CREATE TABLE IF NOT EXISTS public."activityParticipants"
(
    "userId" integer NOT NULL,
    "activityId" integer NOT NULL,
    status status_participant,
    "joinedAt" timestamp with time zone NOT NULL,
    CONSTRAINT "UserParty_pkey" PRIMARY KEY ("userId", "activityId")
);


CREATE TABLE IF NOT EXISTS public.request
(
    "fromUserId" integer NOT NULL,
    "activityId" integer NOT NULL,
    message character varying(255),
    "requestedAt" timestamp with time zone NOT NULL,
    PRIMARY KEY ("fromUserId", "activityId")
);


CREATE TABLE IF NOT EXISTS public.categories
(
    "categoryId" SERIAL PRIMARY KEY,
    name character varying(24) NOT NULL,
    description text
);

ALTER TABLE IF EXISTS public.activities
    ADD CONSTRAINT "hostId" FOREIGN KEY ("hostUserId")
    REFERENCES public."user" ("userId") MATCH SIMPLE
    ON UPDATE CASCADE
    ON DELETE CASCADE;


ALTER TABLE IF EXISTS public.activities
    ADD CONSTRAINT "categoryId" FOREIGN KEY ("categoryId")
    REFERENCES public.categories ("categoryId") MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
    NOT VALID;


ALTER TABLE IF EXISTS public."activityParticipants"
    ADD CONSTRAINT "activityId" FOREIGN KEY ("activityId")
    REFERENCES public.activities ("activityId") MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE CASCADE;


ALTER TABLE IF EXISTS public."activityParticipants"
    ADD CONSTRAINT "userId" FOREIGN KEY ("userId")
    REFERENCES public."user" ("userId") MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE CASCADE;


ALTER TABLE IF EXISTS public.request
    ADD CONSTRAINT "fromUserId" FOREIGN KEY ("fromUserId")
    REFERENCES public."user" ("userId") MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE CASCADE
    NOT VALID;


ALTER TABLE IF EXISTS public.request
    ADD CONSTRAINT "activityId" FOREIGN KEY ("activityId")
    REFERENCES public.activities ("activityId") MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE CASCADE
    NOT VALID;

END;

-- INSERT DATA
insert into "user" values
(nextval('users_sequence'), 'Oat', 'oat@email.com', 'admin', 'A12dbf14hjlk09888ddsafgSDF','1', '2020-09-27', 'phone','line',now(),now()),
(nextval('users_sequence'), 'Vinncent', 'Vinncent@email.com', 'user', '45FGFdsf093lfgffflDSAFDSAF43','1', '2020-09-27', 'phone','line',now(),now()),
(nextval('users_sequence'), 'NewUser', 'asdfsda@email.com', 'user', 'fd43DDSfgFDJkmAF43','1', '2020-09-27', 'phone','line',now(),now()),
(nextval('users_sequence'), 'Mbappe', 's77777@email.com', 'user', '12sfdSDww232trhy3DDSfgFDJkmAF43','1', '2020-09-27', 'phone','line',now(),now()),
(nextval('users_sequence'), 'Haaland', '34435DFDFA@email.com', 'user', 'df3DSF989fdghs','1', '2020-09-27', 'phone','line',now(),now());

insert into "categories" values
(nextval('categories_sequence'), 'Football', '22 players 11 each team'),
(nextval('categories_sequence'), 'Volleyball', '6 players Volleyball'),
(nextval('categories_sequence'), 'Tennis', '1v1 Tennis');

insert into "activities" values
(nextval('activities_sequence'), 1, 1, 'Football Party', 'Description', 'Place', now(), 40, now(), now(), 22),
(nextval('activities_sequence'), 2, 1, 'Football After Class', 'DescriptionZ', 'Place2', now(), 100, now(), now(), 22),
(nextval('activities_sequence'), 3, 2, 'Come play Volley!!', 'วอลเลย์กันเถอะ', 'สนาม A', now(), 120, now(), now(), 12),
(nextval('activities_sequence'), 3, 3, 'ใครว่างมาเทนนิสที่สนามหลังมอ', 'สนามหลังมอ เทนนิส 1v1', 'สนามหลังมอ', now(), 100, now(), now(), 12);

insert into "activityParticipants" values
(1, 1, 'ready', now()),
(2, 1, 'ready', now()),
(1, 2, 'ready', now()),
(2, 2, 'ready', now());

insert into "request" values
(4, 1, 'อยากพริ้วว่ะ', now()),
(5, 4, 'ผมเล่นเทนนิสโคตรโหด', now());