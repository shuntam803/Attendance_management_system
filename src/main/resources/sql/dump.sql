--
-- PostgreSQL database dump
--

\restrict AmYMbbmujPsVvITrKD9TFTtsZzCgndV7diUwO34pFhZwOJxo3TV4BeNASUxm8ba

-- Dumped from database version 13.22
-- Dumped by pg_dump version 13.22

-- Started on 2025-09-18 11:42:20 JST

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
-- TOC entry 201 (class 1259 OID 756986)
-- Name: m_employee; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.m_employee (
                                   employee_code character varying(50) NOT NULL,
                                   last_name character varying(10) NOT NULL,
                                   first_name character varying(50) NOT NULL,
                                   last_kana_name character varying(50) NOT NULL,
                                   first_kana_name character varying(50) NOT NULL,
                                   gender integer NOT NULL,
                                   birth_day date NOT NULL,
                                   section_code character varying(10) NOT NULL,
                                   hire_date date NOT NULL,
                                   confirmation character varying(50),
                                   password character varying(50)
);


ALTER TABLE public.m_employee OWNER TO postgres;

--
-- TOC entry 200 (class 1259 OID 756981)
-- Name: m_section; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.m_section (
                                  section_code character varying(10) NOT NULL,
                                  section_name character varying(50) NOT NULL
);


ALTER TABLE public.m_section OWNER TO postgres;

--
-- TOC entry 202 (class 1259 OID 756996)
-- Name: m_user; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.m_user (
                               user_id character varying(50) NOT NULL,
                               password character varying(50) NOT NULL,
                               confirmation character varying(50)
);


ALTER TABLE public.m_user OWNER TO postgres;

--
-- TOC entry 203 (class 1259 OID 757001)
-- Name: syain_table; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.syain_table (
                                    syain_no integer NOT NULL,
                                    syain_name character varying(30)
);


ALTER TABLE public.syain_table OWNER TO postgres;

--
-- TOC entry 204 (class 1259 OID 757006)
-- Name: t_work_time; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.t_work_time (
                                    employee_code character varying(50),
                                    work_date date,
                                    start_time time without time zone,
                                    finish_time time without time zone,
                                    break_start_time time without time zone,
                                    break_finish_time time without time zone
);


ALTER TABLE public.t_work_time OWNER TO postgres;

--
-- TOC entry 3000 (class 0 OID 756986)
-- Dependencies: 201
-- Data for Name: m_employee; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.m_employee (employee_code, last_name, first_name, last_kana_name, first_kana_name, gender, birth_day, section_code, hire_date, confirmation, password) FROM stdin;
E0241	織田	信長	おだ	のぶなが	0	2022-02-07	1001	2022-02-18	aaaaaaaa	aaaaaaaa
E0237	福岡	太朗	ふくおか	たろう	1	2008-02-02	1002	2022-02-02	\N	aaaa1111
E0236	👹管理	太郎	やまだ	たろう	0	1995-02-09	1003	2022-02-16	E0236	aaaa1111
E0240	安倍	晋三	あべ	しんぞう	0	2022-02-09	1001	2022-02-16	\N	aaaaaaaa
\.


--
-- TOC entry 2999 (class 0 OID 756981)
-- Dependencies: 200
-- Data for Name: m_section; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.m_section (section_code, section_name) FROM stdin;
1001	営業部
1002	開発部
1003	総務部
\.


--
-- TOC entry 3001 (class 0 OID 756996)
-- Dependencies: 202
-- Data for Name: m_user; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.m_user (user_id, password, confirmation) FROM stdin;
1111	1a5faf512691635f5290493695745bef	aaaa1111
1112	1a5faf512691635f5290493695745bef	\N
1212	1a5faf512691635f5290493695745bef	\N
1234	aaaa1111
1235	abcd1234
1236	aaaa1111	\N
1237	1a5faf512691635f5290493695745bef	aaaa1111
2222	d6b75947c02681f31c90c668c46bf6b8	\N
aaaa1111	1a5faf512691635f5290493695745bef	\N
E1111	d6b75947c02681f31c90c668c46bf6b8	\N
E2222	d6b75947c02681f31c90c668c46bf6b8	aaaaaaaa
9999	9a9dca154c72469b25460e08e9f8d0d7	\N
\.


--
-- TOC entry 3002 (class 0 OID 757001)
-- Dependencies: 203
-- Data for Name: syain_table; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.syain_table (syain_no, syain_name) FROM stdin;
1	福岡 辰徳
2	薬院 達也
3	大濠 千代子
4	高宮 早苗
5	大橋 連
6	井尻 鉄平
7	平尾 徹子
8	朝倉 亮
9	黒崎 郁恵
10	清川 聖也
\.


--
-- TOC entry 3003 (class 0 OID 757006)
-- Dependencies: 204
-- Data for Name: t_work_time; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.t_work_time (employee_code, work_date, start_time, finish_time, break_start_time, break_finish_time) FROM stdin;
1234	2022-02-16	22:12:53	22:23:53	22:14:32	22:14:45
E0236	2022-02-19	04:42:01	04:42:13	04:42:06	04:42:09
E0236	2025-09-17	14:47:03.569906	14:47:28.355219	14:47:24.018719	14:47:26.597369
E0236	2025-09-16	09:47:03	18:47:28	12:00:24	13:00:20
\.


--
-- TOC entry 2861 (class 2606 OID 756990)
-- Name: m_employee m_employee_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.m_employee
    ADD CONSTRAINT m_employee_pkey PRIMARY KEY (employee_code);


--
-- TOC entry 2859 (class 2606 OID 757014)
-- Name: m_section m_section_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.m_section
    ADD CONSTRAINT m_section_pkey PRIMARY KEY (section_code);


--
-- TOC entry 2863 (class 2606 OID 757000)
-- Name: m_user m_user_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.m_user
    ADD CONSTRAINT m_user_pkey PRIMARY KEY (user_id);


--
-- TOC entry 2865 (class 2606 OID 757005)
-- Name: syain_table syain_table_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.syain_table
    ADD CONSTRAINT syain_table_pkey PRIMARY KEY (syain_no);


--
-- TOC entry 2867 (class 2606 OID 757010)
-- Name: t_work_time t_work_time_uk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.t_work_time
    ADD CONSTRAINT t_work_time_uk UNIQUE (employee_code, work_date, start_time, finish_time, break_start_time, break_finish_time);


--
-- TOC entry 2868 (class 2606 OID 757023)
-- Name: m_employee fk_employee_section; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.m_employee
    ADD CONSTRAINT fk_employee_section FOREIGN KEY (section_code) REFERENCES public.m_section(section_code);


-- Completed on 2025-09-18 11:42:25 JST

--
-- PostgreSQL database dump complete
--

\unrestrict AmYMbbmujPsVvITrKD9TFTtsZzCgndV7diUwO34pFhZwOJxo3TV4BeNASUxm8ba

