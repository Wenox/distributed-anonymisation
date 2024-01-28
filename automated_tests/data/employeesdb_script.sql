--
-- PostgreSQL database dump
--

-- Dumped from database version 14.7 (Homebrew)
-- Dumped by pg_dump version 14.7 (Homebrew)

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

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: employees; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.employees (
    id integer NOT NULL,
    name character varying(255),
    surname character varying(255),
    date_of_birth date,
    salary numeric(10,2),
    phone_number character varying(15),
    job character varying(255),
    title character varying(255)
);


ALTER TABLE public.employees OWNER TO postgres;

--
-- Name: employees_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.employees_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.employees_id_seq OWNER TO postgres;

--
-- Name: employees_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.employees_id_seq OWNED BY public.employees.id;


--
-- Name: employees id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.employees ALTER COLUMN id SET DEFAULT nextval('public.employees_id_seq'::regclass);


--
-- Data for Name: employees; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.employees (id, name, surname, date_of_birth, salary, phone_number, job, title) FROM stdin;
1	John	Doe	1980-03-21	80000.00	555-123-4567	Software Engineer	Senior Engineer
2	Jane	Smith	1985-08-15	75000.00	555-987-6543	Data Analyst	Analyst
3	Alice	Johnson	1990-06-11	90000.00	555-444-5555	Product Manager	Manager
4	Bob	Brown	1983-12-05	95000.00	555-222-3333	UX Designer	Senior Designer
5	Charlie	Davis	1978-11-30	100000.00	555-111-9999	CTO	Executive
6	Diana	Garcia	1992-04-16	70000.00	555-888-7777	Web Developer	Developer
7	Eva	Miller	1987-07-03	85000.00	555-666-5555	QA Engineer	Senior QA
8	Frank	Wilson	1982-10-25	60000.00	555-333-2222	Technical Support	Support Specialist
9	Grace	Rodriguez	1993-02-09	55000.00	555-777-8888	Marketing Specialist	Specialist
10	Henry	Martinez	1989-09-27	65000.00	555-666-9999	Sales Manager	Manager
\.


--
-- Name: employees_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.employees_id_seq', 10, true);


--
-- Name: employees employees_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.employees
    ADD CONSTRAINT employees_pkey PRIMARY KEY (id);


--
-- PostgreSQL database dump complete
--

