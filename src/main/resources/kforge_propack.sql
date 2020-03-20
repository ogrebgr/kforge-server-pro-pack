--
-- PostgreSQL database dump
--

-- Dumped from database version 11.7 (Ubuntu 11.7-2.pgdg18.04+1)
-- Dumped by pg_dump version 11.7 (Ubuntu 11.7-2.pgdg18.04+1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: kforge_propack; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA kforge_propack;


ALTER SCHEMA kforge_propack OWNER TO postgres;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: admin_user_scram; Type: TABLE; Schema: kforge_propack; Owner: postgres
--

CREATE TABLE kforge_propack.admin_user_scram (
    id integer NOT NULL,
    "user" integer NOT NULL,
    username character varying(255) NOT NULL,
    salt bytea NOT NULL,
    server_key bytea NOT NULL,
    stored_key bytea NOT NULL,
    iterations integer NOT NULL,
    username_lc character varying(255) NOT NULL
);


ALTER TABLE kforge_propack.admin_user_scram OWNER TO postgres;

--
-- Name: admin_user_scram_id_seq; Type: SEQUENCE; Schema: kforge_propack; Owner: postgres
--

CREATE SEQUENCE kforge_propack.admin_user_scram_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE kforge_propack.admin_user_scram_id_seq OWNER TO postgres;

--
-- Name: admin_user_scram_id_seq; Type: SEQUENCE OWNED BY; Schema: kforge_propack; Owner: postgres
--

ALTER SEQUENCE kforge_propack.admin_user_scram_id_seq OWNED BY kforge_propack.admin_user_scram.id;


--
-- Name: admin_users; Type: TABLE; Schema: kforge_propack; Owner: postgres
--

CREATE TABLE kforge_propack.admin_users (
    id integer NOT NULL,
    is_disabled boolean NOT NULL,
    is_superadmin boolean NOT NULL,
    name character varying
);


ALTER TABLE kforge_propack.admin_users OWNER TO postgres;

--
-- Name: admin_users_id_seq; Type: SEQUENCE; Schema: kforge_propack; Owner: postgres
--

CREATE SEQUENCE kforge_propack.admin_users_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE kforge_propack.admin_users_id_seq OWNER TO postgres;

--
-- Name: admin_users_id_seq; Type: SEQUENCE OWNED BY; Schema: kforge_propack; Owner: postgres
--

ALTER SEQUENCE kforge_propack.admin_users_id_seq OWNED BY kforge_propack.admin_users.id;


--
-- Name: jettysessions; Type: TABLE; Schema: kforge_propack; Owner: kforge_propack
--

CREATE TABLE kforge_propack.jettysessions (
    sessionid character varying(120) NOT NULL,
    contextpath character varying(60) NOT NULL,
    virtualhost character varying(60) NOT NULL,
    lastnode character varying(60),
    accesstime bigint,
    lastaccesstime bigint,
    createtime bigint,
    cookietime bigint,
    lastsavedtime bigint,
    expirytime bigint,
    maxinterval bigint,
    map bytea
);


ALTER TABLE kforge_propack.jettysessions OWNER TO kforge_propack;

--
-- Name: user_blowfish; Type: TABLE; Schema: kforge_propack; Owner: kforge_propack
--

CREATE TABLE kforge_propack.user_blowfish (
    "user" integer NOT NULL,
    username character varying(255) NOT NULL,
    password character varying(255) NOT NULL,
    username_lc character varying(255) NOT NULL
);


ALTER TABLE kforge_propack.user_blowfish OWNER TO kforge_propack;

--
-- Name: user_blowfish2; Type: TABLE; Schema: kforge_propack; Owner: kforge_propack
--

CREATE TABLE kforge_propack.user_blowfish2 (
    "user" integer NOT NULL,
    username character varying(255) NOT NULL,
    password character varying(255) NOT NULL,
    username_lc character varying(255) NOT NULL
);


ALTER TABLE kforge_propack.user_blowfish2 OWNER TO kforge_propack;

--
-- Name: user_screen_names; Type: TABLE; Schema: kforge_propack; Owner: kforge_propack
--

CREATE TABLE kforge_propack.user_screen_names (
    "user" integer NOT NULL,
    screen_name character varying(255) NOT NULL,
    screen_name_lc character varying(255) NOT NULL
);


ALTER TABLE kforge_propack.user_screen_names OWNER TO kforge_propack;

--
-- Name: users; Type: TABLE; Schema: kforge_propack; Owner: kforge_propack
--

CREATE TABLE kforge_propack.users (
    id integer NOT NULL,
    is_disabled boolean NOT NULL,
    login_type integer NOT NULL
);


ALTER TABLE kforge_propack.users OWNER TO kforge_propack;

--
-- Name: users_id_seq; Type: SEQUENCE; Schema: kforge_propack; Owner: kforge_propack
--

CREATE SEQUENCE kforge_propack.users_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE kforge_propack.users_id_seq OWNER TO kforge_propack;

--
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: kforge_propack; Owner: kforge_propack
--

ALTER SEQUENCE kforge_propack.users_id_seq OWNED BY kforge_propack.users.id;


--
-- Name: admin_user_scram id; Type: DEFAULT; Schema: kforge_propack; Owner: postgres
--

ALTER TABLE ONLY kforge_propack.admin_user_scram ALTER COLUMN id SET DEFAULT nextval('kforge_propack.admin_user_scram_id_seq'::regclass);


--
-- Name: admin_users id; Type: DEFAULT; Schema: kforge_propack; Owner: postgres
--

ALTER TABLE ONLY kforge_propack.admin_users ALTER COLUMN id SET DEFAULT nextval('kforge_propack.admin_users_id_seq'::regclass);


--
-- Name: users id; Type: DEFAULT; Schema: kforge_propack; Owner: kforge_propack
--

ALTER TABLE ONLY kforge_propack.users ALTER COLUMN id SET DEFAULT nextval('kforge_propack.users_id_seq'::regclass);


--
-- Name: admin_user_scram admin_user_scram_pkey; Type: CONSTRAINT; Schema: kforge_propack; Owner: postgres
--

ALTER TABLE ONLY kforge_propack.admin_user_scram
    ADD CONSTRAINT admin_user_scram_pkey PRIMARY KEY (id);


--
-- Name: admin_users admin_users_pkey; Type: CONSTRAINT; Schema: kforge_propack; Owner: postgres
--

ALTER TABLE ONLY kforge_propack.admin_users
    ADD CONSTRAINT admin_users_pkey PRIMARY KEY (id);


--
-- Name: jettysessions jettysessions_pkey; Type: CONSTRAINT; Schema: kforge_propack; Owner: kforge_propack
--

ALTER TABLE ONLY kforge_propack.jettysessions
    ADD CONSTRAINT jettysessions_pkey PRIMARY KEY (sessionid, contextpath, virtualhost);


--
-- Name: user_blowfish uq_user_username; Type: CONSTRAINT; Schema: kforge_propack; Owner: kforge_propack
--

ALTER TABLE ONLY kforge_propack.user_blowfish
    ADD CONSTRAINT uq_user_username UNIQUE (username);


--
-- Name: user_blowfish2 uq_user_username2; Type: CONSTRAINT; Schema: kforge_propack; Owner: kforge_propack
--

ALTER TABLE ONLY kforge_propack.user_blowfish2
    ADD CONSTRAINT uq_user_username2 UNIQUE (username);


--
-- Name: user_blowfish uq_user_username_lc; Type: CONSTRAINT; Schema: kforge_propack; Owner: kforge_propack
--

ALTER TABLE ONLY kforge_propack.user_blowfish
    ADD CONSTRAINT uq_user_username_lc UNIQUE (username_lc);


--
-- Name: user_blowfish2 uq_user_username_lc2; Type: CONSTRAINT; Schema: kforge_propack; Owner: kforge_propack
--

ALTER TABLE ONLY kforge_propack.user_blowfish2
    ADD CONSTRAINT uq_user_username_lc2 UNIQUE (username_lc);


--
-- Name: admin_user_scram uq_username; Type: CONSTRAINT; Schema: kforge_propack; Owner: postgres
--

ALTER TABLE ONLY kforge_propack.admin_user_scram
    ADD CONSTRAINT uq_username UNIQUE (username_lc);


--
-- Name: CONSTRAINT uq_username ON admin_user_scram; Type: COMMENT; Schema: kforge_propack; Owner: postgres
--

COMMENT ON CONSTRAINT uq_username ON kforge_propack.admin_user_scram IS 'гфд
';


--
-- Name: user_blowfish user_blowfish_pkey; Type: CONSTRAINT; Schema: kforge_propack; Owner: kforge_propack
--

ALTER TABLE ONLY kforge_propack.user_blowfish
    ADD CONSTRAINT user_blowfish_pkey PRIMARY KEY ("user");


--
-- Name: user_blowfish2 user_blowfish_pkey2; Type: CONSTRAINT; Schema: kforge_propack; Owner: kforge_propack
--

ALTER TABLE ONLY kforge_propack.user_blowfish2
    ADD CONSTRAINT user_blowfish_pkey2 PRIMARY KEY ("user");


--
-- Name: user_screen_names user_screen_names_pkey; Type: CONSTRAINT; Schema: kforge_propack; Owner: kforge_propack
--

ALTER TABLE ONLY kforge_propack.user_screen_names
    ADD CONSTRAINT user_screen_names_pkey PRIMARY KEY ("user");


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: kforge_propack; Owner: kforge_propack
--

ALTER TABLE ONLY kforge_propack.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: i_screen_name_lc; Type: INDEX; Schema: kforge_propack; Owner: kforge_propack
--

CREATE INDEX i_screen_name_lc ON kforge_propack.user_screen_names USING btree (screen_name_lc);


--
-- Name: idx_jettysessions_expiry; Type: INDEX; Schema: kforge_propack; Owner: kforge_propack
--

CREATE INDEX idx_jettysessions_expiry ON kforge_propack.jettysessions USING btree (expirytime);


--
-- Name: idx_jettysessions_session; Type: INDEX; Schema: kforge_propack; Owner: kforge_propack
--

CREATE INDEX idx_jettysessions_session ON kforge_propack.jettysessions USING btree (sessionid, contextpath);


--
-- Name: SCHEMA kforge_propack; Type: ACL; Schema: -; Owner: postgres
--

GRANT USAGE ON SCHEMA kforge_propack TO kforge_propack;


--
-- Name: TABLE admin_user_scram; Type: ACL; Schema: kforge_propack; Owner: postgres
--

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE kforge_propack.admin_user_scram TO kforge_propack;


--
-- Name: SEQUENCE admin_user_scram_id_seq; Type: ACL; Schema: kforge_propack; Owner: postgres
--

GRANT ALL ON SEQUENCE kforge_propack.admin_user_scram_id_seq TO kforge_propack;


--
-- Name: TABLE admin_users; Type: ACL; Schema: kforge_propack; Owner: postgres
--

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE kforge_propack.admin_users TO kforge_propack;


--
-- Name: SEQUENCE admin_users_id_seq; Type: ACL; Schema: kforge_propack; Owner: postgres
--

GRANT ALL ON SEQUENCE kforge_propack.admin_users_id_seq TO kforge_propack;


--
-- PostgreSQL database dump complete
--

