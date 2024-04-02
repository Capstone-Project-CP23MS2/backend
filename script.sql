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
DROP TABLE IF EXISTS "notification" CASCADE;
DROP TABLE IF EXISTS "userInterest" CASCADE;
DROP TABLE IF EXISTS "location" CASCADE;
DROP TABLE IF EXISTS "reviewActivity" CASCADE;
DROP TABLE IF EXISTS "reviewUser" CASCADE;

DROP TYPE IF EXISTS gender_user;
DROP TYPE IF EXISTS status_participant;
DROP TYPE IF EXISTS role_user;
DROP TYPE IF EXISTS type_notification;

DROP SEQUENCE IF EXISTS users_sequence;
DROP SEQUENCE IF EXISTS activities_sequence;
DROP SEQUENCE IF EXISTS categories_sequence;
DROP SEQUENCE IF EXISTS notifications_sequence;
DROP SEQUENCE IF EXISTS locations_sequence;
DROP SEQUENCE IF EXISTS reviews_activity_sequence;
DROP SEQUENCE IF EXISTS reviews_user_sequence;

CREATE SEQUENCE users_sequence START 1;
CREATE SEQUENCE activities_sequence START 1;
CREATE SEQUENCE categories_sequence START 1;
CREATE SEQUENCE notifications_sequence START 1;
CREATE SEQUENCE locations_sequence START 1;
CREATE SEQUENCE reviews_activity_sequence START 1;
CREATE SEQUENCE reviews_user_sequence START 1;


CREATE TABLE IF NOT EXISTS public.activities
(
    "activityId" SERIAL PRIMARY KEY,
    "hostUserId" integer NOT NULL,
    "categoryId" integer NOT NULL,
    title character varying(100) COLLATE pg_catalog."default" NOT NULL,
    description text,
	"locationId" integer NOT NULL,
    "dateTime" timestamp with time zone NOT NULL,
    duration integer NOT NULL,
    "createdAt" timestamp with time zone NOT NULL,
    "updatedAt" timestamp with time zone NOT NULL,
	"noOfMembers" integer
);

CREATE TYPE gender_user AS ENUM ('Male', 'Female', 'Other', 'NotApplicable', 'Unknown');
CREATE TYPE role_user AS ENUM ('admin', 'user');
CREATE TABLE IF NOT EXISTS public."user"
(
    "userId" SERIAL PRIMARY KEY,
    username character varying(40) COLLATE pg_catalog."default" NOT NULL,
	email character varying(40),
	"role" role_user,
    "profilePicture" text COLLATE pg_catalog."default",
    gender gender_user,
    "dateOfBirth" date,
	"locationId" integer NOT NULL,
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

-- CREATE TABLE IF NOT EXISTS public."userInterests"
-- (
--     "userId" serial NOT NULL,
--     "categoryId" serial NOT NULL,
--     PRIMARY KEY ("userId", "categoryId")
-- );

CREATE TABLE IF NOT EXISTS public.location
(
    "locationId" serial NOT NULL,
    name character varying(20) NOT NULL,
	latitude numeric,
    longitude numeric,
    PRIMARY KEY ("locationId")
);

CREATE TYPE type_notification AS ENUM ('invite', 'join', 'leave', 'request', 'recommend', 'review', 'activity_start', 'activity_end');
CREATE TABLE IF NOT EXISTS public.notification
(
    "notificationId" serial NOT NULL,
    "targetId" integer,
    unread boolean,
    type character varying,
    message character varying,
    "createdAt" timestamp with time zone,
    PRIMARY KEY ("notificationId")
);

CREATE TABLE IF NOT EXISTS public."userInterest"
(
    "userId" integer NOT NULL,
    "categoryId" integer NOT NULL,
    PRIMARY KEY ("userId", "categoryId")
);

CREATE TABLE IF NOT EXISTS public."reviewActivity"
(
    "reviewId" serial NOT NULL,
    "activityId" integer NOT NULL,
    "userId" integer NOT NULL,
    rating integer,
    comment character varying,
    "createdAt" timestamp with time zone,
    PRIMARY KEY ("reviewId")
);

CREATE TABLE IF NOT EXISTS public."reviewUser"
(
    "reviewId" serial NOT NULL,
    "userId" integer NOT NULL,
    "reviewerId" integer NOT NULL,
    comment character varying,
    "createdAt" timestamp with time zone,
    PRIMARY KEY ("reviewId")
);

ALTER TABLE IF EXISTS public."user"
    ADD CONSTRAINT "locationId" FOREIGN KEY ("locationId")
    REFERENCES public.location ("locationId") MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
    NOT VALID;

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
	
ALTER TABLE IF EXISTS public.activities
    ADD CONSTRAINT "locationId" FOREIGN KEY ("locationId")
    REFERENCES public.location ("locationId") MATCH SIMPLE
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

-- ALTER TABLE IF EXISTS public."userInterests"
--     ADD CONSTRAINT "userId" FOREIGN KEY ("userId")
--     REFERENCES public."user" ("userId") MATCH SIMPLE
--     ON UPDATE NO ACTION
--     ON DELETE NO ACTION
--     NOT VALID;


-- ALTER TABLE IF EXISTS public."userInterests"
--     ADD CONSTRAINT "categoryId" FOREIGN KEY ("categoryId")
--     REFERENCES public.categories ("categoryId") MATCH SIMPLE
--     ON UPDATE NO ACTION
--     ON DELETE NO ACTION
--     NOT VALID;
	
ALTER TABLE IF EXISTS public.notification
    ADD CONSTRAINT "targetId" FOREIGN KEY ("targetId")
    REFERENCES public."user" ("userId") MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
    NOT VALID;

ALTER TABLE IF EXISTS public."userInterest"
    ADD CONSTRAINT "userId" FOREIGN KEY ("userId")
    REFERENCES public."user" ("userId") MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
    NOT VALID;


ALTER TABLE IF EXISTS public."userInterest"
    ADD CONSTRAINT "categoryId" FOREIGN KEY ("categoryId")
    REFERENCES public.categories ("categoryId") MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
    NOT VALID;
	
ALTER TABLE IF EXISTS public.reviewActivity
    ADD CONSTRAINT "activityId" FOREIGN KEY ("activityId")
    REFERENCES public.activities ("activityId") MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
    NOT VALID;


ALTER TABLE IF EXISTS public.reviewActivity
    ADD CONSTRAINT "userId" FOREIGN KEY ("userId")
    REFERENCES public."user" ("userId") MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
    NOT VALID;
	
ALTER TABLE IF EXISTS public."reviewUser"
    ADD CONSTRAINT "userId" FOREIGN KEY ("userId")
    REFERENCES public."user" ("userId") MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
    NOT VALID;


ALTER TABLE IF EXISTS public."reviewUser"
    ADD CONSTRAINT "reviewerId" FOREIGN KEY ("reviewerId")
    REFERENCES public."user" ("userId") MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
    NOT VALID;

END;

-- INSERT DATA
insert into "location" values
(nextval('locations_sequence'), 'Bangkok', 21.124325, 21.1111111),
(nextval('locations_sequence'), 'Kam Pang Phet', 234.546546534, 11),
(nextval('locations_sequence'), 'KMUTT', 13.6512522, 100.494061),
(nextval('locations_sequence'), 'The Nine Center', 13.7414219, 100.6199585);
(nextval('locations_sequence'), 'Siam Paragon', 13.7457749, 100.5318268);

insert into "user" values
(nextval('users_sequence'), 'Oat', 'oat@email.com', 'admin', 'A12dbf14hjlk09888ddsafgSDF','Male', '2020-09-27', 1, 'phone','line',now(),now()),
(nextval('users_sequence'), 'Vinncent', 'Vinncent@email.com', 'user', '45FGFdsf093lfgffflDSAFDSAF43','Female', '2020-09-27', 1, 'phone','line',now(),now()),
(nextval('users_sequence'), 'NewUser', 'asdfsda@email.com', 'user', 'fd43DDSfgFDJkmAF43','Other', '2020-09-27', 2, 'phone','line',now(),now()),
(nextval('users_sequence'), 'Mbappe', 's77777@email.com', 'user', '12sfdSDww232trhy3DDSfgFDJkmAF43','NotApplicable', '2020-09-27', 1, 'phone','line',now(),now()),
(nextval('users_sequence'), 'Haaland', '34435DFDFA@email.com', 'user', 'df3DSF989fdghs','Unknown', '2020-09-27', 2, 'phone','line',now(),now()),
(nextval('users_sequence'), 'Yuthasart', 'yuthasart51@gmail.com', 'admin', 'df3DSF989fdghs','Unknown', '2020-09-27', 1, 'phone','line',now(),now()),
(nextval('users_sequence'), 'chic', 'chickenforregis1@gmail.com', 'user', 'df3DSF989fdghs','Unknown', '2020-09-27', 2, 'phone','line',now(),now()),
(nextval('users_sequence'), 'ph', 'chumphu.phumin@gmail.com', 'admin', 'df3DSF989fdghs','Unknown', '2020-09-27', 2, 'phone','line',now(),now());

insert into "categories" values
(nextval('categories_sequence'), 'Football', '22 players 11 each team'),
(nextval('categories_sequence'), 'Volleyball', '6 players Volleyball'),
(nextval('categories_sequence'), 'Tennis', '1v1 Tennis');

insert into "activities" values
(nextval('activities_sequence'), 1, 1, 'Football Party', 'Welcome to football party', 3, now(), 40, now(), now(), 22),
(nextval('activities_sequence'), 2, 1, 'Football After Class', 'join use to play football after class', 4, now(), 100, now(), now(), 22),
(nextval('activities_sequence'), 3, 2, 'Come play Volley!!', 'วอลเลย์กันเถอะ', 5, now(), 120, now(), now(), 12),
(nextval('activities_sequence'), 3, 3, 'ใครว่างมาเทนนิสที่สนามหลังมอ', 'สนามหลังมอ เทนนิส 1v1', 3, now(), 100, now(), now(), 12);

insert into "activityParticipants" values
(1, 1, 'ready', now()),
(2, 1, 'ready', now()),
(1, 2, 'ready', now()),
(2, 2, 'ready', now()),
(3, 3, 'ready', now()),
(3, 4, 'ready', now());

insert into "request" values
(4, 1, 'อยากพริ้วว่ะ', now()),
(5, 4, 'ผมเล่นเทนนิสโคตรโหด', now());

insert into "userInterest" values
(1, 3),
(2, 1);

insert into "notification" values
(nextval('notifications_sequence'), 1, true, 'join', 'asdfadsfasdfasd', now()),
(nextval('notifications_sequence'), 2, false, 'leave', '23213', now());

insert into "reviewActivity" values
(nextval('reviews_activity_sequence'), 1, 1, 5, 'comment', now()),
(nextval('reviews_activity_sequence'), 2, 6, 5, 'comment2', now());

insert into "reviewUser" values
(nextval('reviews_user_sequence'), 1, 2, 'comment3', now()),
(nextval('reviews_user_sequence'), 4, 3, 'comment4', now());
