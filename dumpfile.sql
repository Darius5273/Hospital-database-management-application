--
-- PostgreSQL database dump
--

-- Dumped from database version 16.0
-- Dumped by pg_dump version 16.0

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
-- Name: admission_services; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.admission_services (
    admission_services_id integer NOT NULL,
    admission_id integer NOT NULL,
    service_id integer NOT NULL
);


ALTER TABLE public.admission_services OWNER TO postgres;

--
-- Name: admission_services_admission_services_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.admission_services_admission_services_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.admission_services_admission_services_id_seq OWNER TO postgres;

--
-- Name: admission_services_admission_services_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.admission_services_admission_services_id_seq OWNED BY public.admission_services.admission_services_id;


--
-- Name: admissions; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.admissions (
    admission_id integer NOT NULL,
    patient_id character varying NOT NULL,
    attending_doctor_id integer NOT NULL,
    disease character varying NOT NULL,
    room_id integer NOT NULL,
    admission_date date NOT NULL,
    discharge_date date,
    medical_bill integer,
    CONSTRAINT admissions_check CHECK ((discharge_date > admission_date)),
    CONSTRAINT admissions_check2 CHECK ((medical_bill > 0))
);


ALTER TABLE public.admissions OWNER TO postgres;

--
-- Name: admissions_admission_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.admissions_admission_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.admissions_admission_id_seq OWNER TO postgres;

--
-- Name: admissions_admission_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.admissions_admission_id_seq OWNED BY public.admissions.admission_id;


--
-- Name: appointments; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.appointments (
    appointment_id integer NOT NULL,
    patient_id character varying NOT NULL,
    doctor_id integer NOT NULL,
    appointment_date date NOT NULL,
    appointment_time time without time zone NOT NULL
);


ALTER TABLE public.appointments OWNER TO postgres;

--
-- Name: appointments_appointment_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.appointments_appointment_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.appointments_appointment_id_seq OWNER TO postgres;

--
-- Name: appointments_appointment_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.appointments_appointment_id_seq OWNED BY public.appointments.appointment_id;


--
-- Name: doctors; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.doctors (
    doctor_id integer NOT NULL,
    specialization character varying(100)
);


ALTER TABLE public.doctors OWNER TO postgres;

--
-- Name: medical_records; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.medical_records (
    record_id integer NOT NULL,
    appointment_id integer NOT NULL,
    diagnosis character varying(255) NOT NULL
);


ALTER TABLE public.medical_records OWNER TO postgres;

--
-- Name: medical_records_record_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.medical_records_record_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.medical_records_record_id_seq OWNER TO postgres;

--
-- Name: medical_records_record_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.medical_records_record_id_seq OWNED BY public.medical_records.record_id;


--
-- Name: patients; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.patients (
    patient_id character varying NOT NULL,
    first_name character varying(100) NOT NULL,
    last_name character varying(100) NOT NULL,
    date_of_birth date NOT NULL,
    gender character varying NOT NULL,
    phone_number character varying NOT NULL,
    address character varying(255) NOT NULL,
    CONSTRAINT patients_check CHECK (((gender)::text = ANY ((ARRAY['male'::character varying, 'female'::character varying])::text[])))
);


ALTER TABLE public.patients OWNER TO postgres;

--
-- Name: patients_patient_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.patients_patient_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.patients_patient_id_seq OWNER TO postgres;

--
-- Name: patients_patient_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.patients_patient_id_seq OWNED BY public.patients.patient_id;


--
-- Name: prescriptions_medicines; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.prescriptions_medicines (
    prescription_id integer NOT NULL,
    medicine character varying,
    dosage character varying,
    frequency character varying,
    record_id integer NOT NULL
);


ALTER TABLE public.prescriptions_medicines OWNER TO postgres;

--
-- Name: prescriptions_medicines_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.prescriptions_medicines_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.prescriptions_medicines_id_seq OWNER TO postgres;

--
-- Name: prescriptions_medicines_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.prescriptions_medicines_id_seq OWNED BY public.prescriptions_medicines.record_id;


--
-- Name: prescriptions_prescription_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.prescriptions_prescription_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.prescriptions_prescription_id_seq OWNER TO postgres;

--
-- Name: prescriptions_prescription_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.prescriptions_prescription_id_seq OWNED BY public.prescriptions_medicines.prescription_id;


--
-- Name: rooms; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.rooms (
    room_id integer NOT NULL,
    type character varying NOT NULL,
    charges_day integer NOT NULL,
    CONSTRAINT rooms_check CHECK ((charges_day > 0))
);


ALTER TABLE public.rooms OWNER TO postgres;

--
-- Name: rooms_room_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.rooms_room_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.rooms_room_id_seq OWNER TO postgres;

--
-- Name: rooms_room_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.rooms_room_id_seq OWNED BY public.rooms.room_id;


--
-- Name: services; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.services (
    service_id integer NOT NULL,
    service_name character varying(255) NOT NULL,
    service_charge integer NOT NULL,
    CONSTRAINT services_check CHECK ((service_charge > 0))
);


ALTER TABLE public.services OWNER TO postgres;

--
-- Name: services_service_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.services_service_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.services_service_id_seq OWNER TO postgres;

--
-- Name: services_service_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.services_service_id_seq OWNED BY public.services.service_id;


--
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    user_id integer NOT NULL,
    first_name character varying(50) NOT NULL,
    last_name character varying(50) NOT NULL,
    username character varying(50) NOT NULL,
    password character varying(100) NOT NULL,
    type character varying NOT NULL,
    CONSTRAINT type_const CHECK ((((type)::text = 'doctor'::text) OR ((type)::text = 'receptionist'::text) OR ((type)::text = 'admin'::text)))
);


ALTER TABLE public.users OWNER TO postgres;

--
-- Name: users_user_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.users_user_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.users_user_id_seq OWNER TO postgres;

--
-- Name: users_user_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.users_user_id_seq OWNED BY public.users.user_id;


--
-- Name: admission_services admission_services_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.admission_services ALTER COLUMN admission_services_id SET DEFAULT nextval('public.admission_services_admission_services_id_seq'::regclass);


--
-- Name: admissions admission_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.admissions ALTER COLUMN admission_id SET DEFAULT nextval('public.admissions_admission_id_seq'::regclass);


--
-- Name: appointments appointment_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.appointments ALTER COLUMN appointment_id SET DEFAULT nextval('public.appointments_appointment_id_seq'::regclass);


--
-- Name: medical_records record_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.medical_records ALTER COLUMN record_id SET DEFAULT nextval('public.medical_records_record_id_seq'::regclass);


--
-- Name: rooms room_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rooms ALTER COLUMN room_id SET DEFAULT nextval('public.rooms_room_id_seq'::regclass);


--
-- Name: services service_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.services ALTER COLUMN service_id SET DEFAULT nextval('public.services_service_id_seq'::regclass);


--
-- Name: users user_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users ALTER COLUMN user_id SET DEFAULT nextval('public.users_user_id_seq'::regclass);


--
-- Data for Name: admission_services; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.admission_services (admission_services_id, admission_id, service_id) FROM stdin;
1	1	1
3	3	2
8	5	1
18	2	3
19	3	9
20	3	8
22	5	5
24	1	4
28	6	7
29	6	10
30	4	2
21	4	7
\.


--
-- Data for Name: admissions; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.admissions (admission_id, patient_id, attending_doctor_id, disease, room_id, admission_date, discharge_date, medical_bill) FROM stdin;
6	297-37-2274	5	lymphocytic leukemia	14	2024-12-20	2024-12-29	3365
4	624-45-1234	8	cerebral aneurysm	12	2024-08-16	2024-08-22	1700
2	428-82-1148	9	psoriasis	7	2024-02-14	2024-02-17	770
5	428-82-1148	8	stroke	12	2024-08-03	2024-08-04	600
1	593-46-6212	5	hepatocellular carcinoma	3	2024-01-05	2024-01-10	1100
3	624-45-1234	6	pneumonia	6	2024-01-10	2024-01-15	1675
7	624-45-1234	6	Aortic aneurysm	5	2024-01-16	\N	\N
\.


--
-- Data for Name: appointments; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.appointments (appointment_id, patient_id, doctor_id, appointment_date, appointment_time) FROM stdin;
8	624-45-1234	6	2024-11-03	15:30:00
9	593-46-6212	5	2024-01-01	08:25:22
5	110-22-1334	4	2024-01-10	10:05:00
6	434-34-8423	3	2024-01-15	11:20:00
10	110-22-1334	8	2024-12-12	13:50:00
12	110-22-1334	4	2024-03-09	09:40:00
13	624-45-1234	9	2024-12-12	17:35:00
17	110-22-1334	3	2024-01-31	09:15:00
18	110-22-1334	3	2024-01-31	09:40:00
19	110-22-1334	3	2024-01-31	10:05:00
20	110-22-1334	3	2024-01-31	10:30:00
21	110-22-1334	3	2024-01-31	10:55:00
22	110-22-1334	3	2024-01-31	11:20:00
23	110-22-1334	3	2024-01-31	11:45:00
24	110-22-1334	3	2024-01-31	12:10:00
25	110-22-1334	3	2024-01-31	12:35:00
14	110-22-1334	3	2024-01-31	08:00:00
26	110-22-1334	3	2024-01-31	13:00:00
27	110-22-1334	3	2024-01-31	13:25:00
28	110-22-1334	3	2024-01-31	13:50:00
29	110-22-1334	3	2024-01-31	14:15:00
30	110-22-1334	3	2024-01-31	14:40:00
31	110-22-1334	3	2024-01-31	15:05:00
32	110-22-1334	3	2024-01-31	15:30:00
33	110-22-1334	3	2024-01-31	15:55:00
35	110-22-1334	3	2024-01-31	16:45:00
36	110-22-1334	3	2024-01-31	17:10:00
37	110-22-1334	3	2024-01-31	17:35:00
38	593-46-6212	9	2024-12-12	15:55:00
11	434-34-8423	5	2024-12-21	14:40:00
16	110-22-1334	3	2024-01-31	08:50:00
15	110-22-1334	3	2024-01-31	08:25:00
39	297-37-2274	8	2024-12-12	08:25:00
7	593-46-6212	5	2024-01-23	11:20:00
\.


--
-- Data for Name: doctors; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.doctors (doctor_id, specialization) FROM stdin;
3	Cardiology
4	Pediatrics
5	Oncology
6	Radiology
8	Neurology
9	Dermatology
11	Neurology
\.


--
-- Data for Name: medical_records; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.medical_records (record_id, appointment_id, diagnosis) FROM stdin;
1	5	Healthy
2	6	Flu
3	7	hepatocellular carcinoma
4	8	pneumonia
5	12	Otitis media
\.


--
-- Data for Name: patients; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.patients (patient_id, first_name, last_name, date_of_birth, gender, phone_number, address) FROM stdin;
110-22-1334	Emma	Johnson	2015-05-15	female	0732881335	123 Main St
434-34-8423	Michael	Williams	1985-08-25	male	0721480254	456 Oak Ave
624-45-1234	William	Taylor	1980-11-12	male	5556667778	567 Pine Ave
593-46-6212	Sophia	Brown	1992-03-20	female	0747293894	789 Elm St
297-37-2274	Saras	Lanjf	1993-07-25	female	0782946435	123 Alt St
428-82-1148	Alabama	Miet	2001-03-12	male	0261953829	43 Iep St
222-45-3212	Ionel	Pop	1999-02-12	male	0247593862	12 A St
\.


--
-- Data for Name: prescriptions_medicines; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.prescriptions_medicines (prescription_id, medicine, dosage, frequency, record_id) FROM stdin;
4	Amoxicilin	20-40 mg/day	every 12 hours for 1 week	5
1	ColdX	1 tablet	every 6 hours for 1 week	2
3	Radiodine	50ml injection	30 minutes before each imaging session	4
2	Oncotreat	20 mg	twice a week	3
\.


--
-- Data for Name: rooms; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.rooms (room_id, type, charges_day) FROM stdin;
2	deluxe	200
4	deluxe	220
8	single AC	175
9	single Non AC	135
10	suite	270
3	single Non AC	100
5	single Non AC	120
7	single Non AC	90
6	deluxe	210
12	single Non AC	150
11	single AC	190
15	single AC	190
14	suite	285
13	single Non AC	140
\.


--
-- Data for Name: services; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.services (service_id, service_name, service_charge) FROM stdin;
1	X-ray	50
2	MRI	100
3	Psoriasis Surgery	500
5	Stroke Rehabilitation	400
6	Cerebral Aneurysm Surgery	600
7	Lymphotic Leukemia Treatment	700
8	Intravenous Ceftriaxone	125
9	Oxygen Therapy	400
10	Complete Blood Count	100
4	Liver Resection	550
11	Chemotherapy	450
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.users (user_id, first_name, last_name, username, password, type) FROM stdin;
1	Admin	Smith	admin_user	admin123	admin
2	Reception	Johnson	reception_user	reception123	receptionist
3	John	Doe	doctor_user1	doctorpass1	doctor
4	Emily	Smith	doctor_user2	doctorpass2	doctor
5	David	Johnson	doctor_user3	doctorpass3	doctor
6	Sophia	Brown	doctor_user4	doctorpass4	doctor
8	Paul	Hens	p_h	pas44	doctor
9	Ella	Lavinescu	ella_bella	par12	doctor
10	Anca-Mirabela	Kelks	kelksmira	24ko	admin
11	Lorena	Buzea	lori	1	doctor
12	Amf	Hjf	223	fd	receptionist
\.


--
-- Name: admission_services_admission_services_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.admission_services_admission_services_id_seq', 30, true);


--
-- Name: admissions_admission_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.admissions_admission_id_seq', 3, true);


--
-- Name: appointments_appointment_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.appointments_appointment_id_seq', 88, true);


--
-- Name: medical_records_record_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.medical_records_record_id_seq', 4, true);


--
-- Name: patients_patient_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.patients_patient_id_seq', 6, true);


--
-- Name: prescriptions_medicines_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.prescriptions_medicines_id_seq', 3, true);


--
-- Name: prescriptions_prescription_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.prescriptions_prescription_id_seq', 3, true);


--
-- Name: rooms_room_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.rooms_room_id_seq', 15, true);


--
-- Name: services_service_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.services_service_id_seq', 10, true);


--
-- Name: users_user_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.users_user_id_seq', 9, true);


--
-- Name: admission_services admission_services_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.admission_services
    ADD CONSTRAINT admission_services_pkey PRIMARY KEY (admission_services_id);


--
-- Name: admissions admissions_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.admissions
    ADD CONSTRAINT admissions_pkey PRIMARY KEY (admission_id);


--
-- Name: appointments appointments_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.appointments
    ADD CONSTRAINT appointments_pkey PRIMARY KEY (appointment_id);


--
-- Name: doctors doctors_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.doctors
    ADD CONSTRAINT doctors_pkey PRIMARY KEY (doctor_id);


--
-- Name: medical_records medical_records_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.medical_records
    ADD CONSTRAINT medical_records_pkey PRIMARY KEY (record_id);


--
-- Name: patients patients_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.patients
    ADD CONSTRAINT patients_pkey PRIMARY KEY (patient_id);


--
-- Name: patients patients_un; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.patients
    ADD CONSTRAINT patients_un UNIQUE (address, first_name, last_name);


--
-- Name: prescriptions_medicines prescriptions_medicines_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.prescriptions_medicines
    ADD CONSTRAINT prescriptions_medicines_pk PRIMARY KEY (prescription_id);


--
-- Name: rooms rooms_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rooms
    ADD CONSTRAINT rooms_pkey PRIMARY KEY (room_id);


--
-- Name: services services_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.services
    ADD CONSTRAINT services_pkey PRIMARY KEY (service_id);


--
-- Name: services services_un; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.services
    ADD CONSTRAINT services_un UNIQUE (service_name);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (user_id);


--
-- Name: users users_un; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_un UNIQUE (first_name, last_name);


--
-- Name: users users_un2; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_un2 UNIQUE (username);


--
-- Name: admission_services admission_services_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.admission_services
    ADD CONSTRAINT admission_services_fk FOREIGN KEY (admission_id) REFERENCES public.admissions(admission_id) ON DELETE CASCADE;


--
-- Name: admission_services admission_services_service_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.admission_services
    ADD CONSTRAINT admission_services_service_id_fkey FOREIGN KEY (service_id) REFERENCES public.services(service_id);


--
-- Name: admissions admissions_attending_doctor_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.admissions
    ADD CONSTRAINT admissions_attending_doctor_id_fkey FOREIGN KEY (attending_doctor_id) REFERENCES public.doctors(doctor_id);


--
-- Name: admissions admissions_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.admissions
    ADD CONSTRAINT admissions_fk FOREIGN KEY (patient_id) REFERENCES public.patients(patient_id) ON UPDATE CASCADE;


--
-- Name: admissions admissions_room_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.admissions
    ADD CONSTRAINT admissions_room_id_fkey FOREIGN KEY (room_id) REFERENCES public.rooms(room_id);


--
-- Name: appointments appointments_doctor_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.appointments
    ADD CONSTRAINT appointments_doctor_id_fkey FOREIGN KEY (doctor_id) REFERENCES public.doctors(doctor_id);


--
-- Name: appointments appointments_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.appointments
    ADD CONSTRAINT appointments_fk FOREIGN KEY (patient_id) REFERENCES public.patients(patient_id) ON UPDATE CASCADE;


--
-- Name: doctors doctors_doctor_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.doctors
    ADD CONSTRAINT doctors_doctor_id_fkey FOREIGN KEY (doctor_id) REFERENCES public.users(user_id);


--
-- Name: medical_records medical_records_appointment_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.medical_records
    ADD CONSTRAINT medical_records_appointment_id_fkey FOREIGN KEY (appointment_id) REFERENCES public.appointments(appointment_id);


--
-- Name: prescriptions_medicines prescriptions_medicines_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.prescriptions_medicines
    ADD CONSTRAINT prescriptions_medicines_fk FOREIGN KEY (record_id) REFERENCES public.medical_records(record_id);


--
-- PostgreSQL database dump complete
--

