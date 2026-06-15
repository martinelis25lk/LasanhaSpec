SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: chronic_issue_preventive_maintenance; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.chronic_issue_preventive_maintenance (
    chronic_issues_id bigint NOT NULL,
    preventive_maintenance character varying(255)
);


--
-- Name: chronic_issues; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.chronic_issues (
    id bigint NOT NULL,
    affected_engines character varying(255),
    affected_years character varying(255),
    cost_max integer,
    cost_min integer,
    created_at timestamp(6) without time zone,
    created_by_user_id bigint,
    description character varying(2000) NOT NULL,
    issue_category character varying(255),
    millage_max integer,
    millage_min integer,
    not_useful_votes integer NOT NULL,
    occurrences integer NOT NULL,
    repair_complexity character varying(255),
    severity character varying(255) NOT NULL,
    status character varying(255),
    title character varying(255) NOT NULL,
    useful_votes integer NOT NULL,
    vehicle_catalog_model_id bigint NOT NULL,
    CONSTRAINT chronic_issues_issue_category_check CHECK (((issue_category)::text = ANY ((ARRAY['MOTOR'::character varying, 'SUSPENSION'::character varying, 'COOLING'::character varying, 'TRANSMISSION'::character varying, 'ELECTRICAL'::character varying, 'BODYWORK'::character varying, 'BRAKES'::character varying, 'TURBO'::character varying, 'FUEL_SYSTEM'::character varying])::text[]))),
    CONSTRAINT chronic_issues_repair_complexity_check CHECK (((repair_complexity)::text = ANY ((ARRAY['DIY'::character varying, 'INTERMEDIATE'::character varying, 'PROFESSIONAL'::character varying])::text[]))),
    CONSTRAINT chronic_issues_severity_check CHECK (((severity)::text = ANY ((ARRAY['LOW'::character varying, 'MEDIUM'::character varying, 'HIGH'::character varying, 'CRITICAL'::character varying])::text[]))),
    CONSTRAINT chronic_issues_status_check CHECK (((status)::text = ANY ((ARRAY['PENDING'::character varying, 'IN_REVIEW'::character varying, 'APPROVED'::character varying, 'REJECTED'::character varying])::text[])))
);


--
-- Name: chronic_issues_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.chronic_issues_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: chronic_issues_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.chronic_issues_id_seq OWNED BY public.chronic_issues.id;


--
-- Name: chronic_issues_symptoms; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.chronic_issues_symptoms (
    chronic_issues_id bigint NOT NULL,
    symptoms character varying(255)
);


--
-- Name: current_setups; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.current_setups (
    id bigint NOT NULL,
    active boolean,
    created_at date,
    engine_stage character varying(255),
    forged_internals boolean,
    reliability character varying(255),
    target_horse_power integer,
    target_torque integer,
    usage_type character varying(255),
    version integer,
    based_on_id bigint,
    user_vehicle_id bigint NOT NULL,
    CONSTRAINT current_setups_engine_stage_check CHECK (((engine_stage)::text = ANY ((ARRAY['STOCK'::character varying, 'STAGE_1'::character varying, 'STAGE_2'::character varying])::text[]))),
    CONSTRAINT current_setups_reliability_check CHECK (((reliability)::text = ANY ((ARRAY['HIGH'::character varying, 'MEDIUM'::character varying, 'LOW'::character varying])::text[]))),
    CONSTRAINT current_setups_usage_type_check CHECK (((usage_type)::text = ANY ((ARRAY['DAILY'::character varying, 'TRACK_DAY'::character varying, 'DRAG'::character varying, 'DRIFT'::character varying, 'MIX'::character varying])::text[])))
);


--
-- Name: current_setups_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.current_setups_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: current_setups_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.current_setups_id_seq OWNED BY public.current_setups.id;


--
-- Name: issue_occurrences; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.issue_occurrences (
    id bigint NOT NULL,
    created_at timestamp(6) without time zone,
    description character varying(255),
    millage_at_occurrence double precision NOT NULL,
    repair_cost double precision,
    updated_at timestamp(6) without time zone,
    issue_id bigint NOT NULL,
    user_vehicle_id bigint NOT NULL
);


--
-- Name: issue_occurrences_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.issue_occurrences_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: issue_occurrences_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.issue_occurrences_id_seq OWNED BY public.issue_occurrences.id;


--
-- Name: issue_votes; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.issue_votes (
    id bigint NOT NULL,
    user_id bigint NOT NULL,
    vote_type character varying(255) NOT NULL,
    issue_id bigint NOT NULL,
    CONSTRAINT issue_votes_vote_type_check CHECK (((vote_type)::text = ANY ((ARRAY['USEFUL'::character varying, 'NOT_USEFUL'::character varying])::text[])))
);


--
-- Name: issue_votes_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.issue_votes_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: issue_votes_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.issue_votes_id_seq OWNED BY public.issue_votes.id;


--
-- Name: setups; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.setups (
    id bigint NOT NULL,
    community_setup boolean,
    created_at date,
    created_by character varying(255),
    engine_stage smallint,
    forged_internals boolean,
    reliability smallint,
    target_aspiration_type character varying(255),
    target_horse_power integer,
    target_torque integer,
    usage smallint,
    vehicle_catalog_model_id bigint NOT NULL,
    CONSTRAINT setups_engine_stage_check CHECK (((engine_stage >= 0) AND (engine_stage <= 2))),
    CONSTRAINT setups_reliability_check CHECK (((reliability >= 0) AND (reliability <= 2))),
    CONSTRAINT setups_target_aspiration_type_check CHECK (((target_aspiration_type)::text = ANY ((ARRAY['NATURALLY_ASPPIRATED'::character varying, 'TURBOCHARGED'::character varying, 'SUPERCHARGED'::character varying])::text[]))),
    CONSTRAINT setups_usage_check CHECK (((usage >= 0) AND (usage <= 4)))
);


--
-- Name: setups_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.setups_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: setups_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.setups_id_seq OWNED BY public.setups.id;


--
-- Name: user_vehicles; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.user_vehicles (
    id bigint NOT NULL,
    active boolean,
    nickname character varying(255),
    user_id bigint,
    vehicle_catalog_model_id bigint NOT NULL,
    current_horse_power integer,
    current_torque integer,
    current_weight integer
);


--
-- Name: user_vehicles_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.user_vehicles_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: user_vehicles_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.user_vehicles_id_seq OWNED BY public.user_vehicles.id;


--
-- Name: users; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.users (
    id bigint NOT NULL,
    email character varying(255) NOT NULL,
    password character varying(255) NOT NULL,
    username character varying(255),
    role character varying(255),
    description character varying(500),
    full_name character varying(255),
    profile_images3key character varying(255),
    profile_image_url character varying(255),
    CONSTRAINT users_role_check CHECK (((role)::text = ANY ((ARRAY['ROLE_USER'::character varying, 'ROLE_ADMIN'::character varying])::text[])))
);


--
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;


--
-- Name: vehicle_images; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.vehicle_images (
    id bigint NOT NULL,
    image_url character varying(255),
    primary_image boolean,
    user_vehicle_id bigint NOT NULL,
    s3key character varying(255)
);


--
-- Name: vehicle_images_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.vehicle_images_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: vehicle_images_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.vehicle_images_id_seq OWNED BY public.vehicle_images.id;


--
-- Name: vehicle_models; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.vehicle_models (
    id bigint NOT NULL,
    aspiration_type character varying(255),
    brand character varying(255),
    engine_code character varying(255),
    factory_horsepower integer,
    factory_torque integer,
    model character varying(255),
    year integer,
    factory_weight integer,
    acceleration0to100 double precision,
    cylinder_count integer,
    displacement double precision,
    drive_type character varying(255),
    fuel_type character varying(255),
    gear_count integer,
    top_speed double precision,
    transmission_model character varying(255),
    transmission_type character varying(255),
    fipe_brand_code character varying(255),
    fipe_model_code character varying(255),
    fipe_year_code character varying(255),
    CONSTRAINT vehicle_models_drive_type_check CHECK (((drive_type)::text = ANY ((ARRAY['AWD'::character varying, 'FWD'::character varying, 'RWD'::character varying])::text[]))),
    CONSTRAINT vehicle_models_fuel_type_check CHECK (((fuel_type)::text = ANY ((ARRAY['GASOLINE'::character varying, 'ETHANOL'::character varying, 'FLEX'::character varying, 'DIESEL'::character varying, 'HYBRID'::character varying, 'ELECTRIC'::character varying])::text[]))),
    CONSTRAINT vehicle_models_transmission_model_check CHECK (((transmission_model)::text = ANY ((ARRAY['AL4'::character varying, 'AISIN_AT6'::character varying, 'ZF_8HP'::character varying, 'DUALOGIC'::character varying, 'I_MOTION'::character varying, 'EASYTRONIC'::character varying, 'DSG'::character varying, 'POWERSHIFT'::character varying, 'CVT_JATCO'::character varying, 'CVT_HONDA'::character varying, 'MANUAL_5'::character varying, 'MANUAL_6'::character varying, 'MANUAL_4'::character varying])::text[]))),
    CONSTRAINT vehicle_models_transmission_type_check CHECK (((transmission_type)::text = ANY ((ARRAY['MANUAL'::character varying, 'AUTOMATIC'::character varying, 'AUTOMATED_MANUAL'::character varying, 'DUAL_CLUTCH'::character varying, 'CVT'::character varying])::text[])))
);


--
-- Name: vehicle_models_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.vehicle_models_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: vehicle_models_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.vehicle_models_id_seq OWNED BY public.vehicle_models.id;


--
-- Name: chronic_issues id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.chronic_issues ALTER COLUMN id SET DEFAULT nextval('public.chronic_issues_id_seq'::regclass);


--
-- Name: current_setups id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.current_setups ALTER COLUMN id SET DEFAULT nextval('public.current_setups_id_seq'::regclass);


--
-- Name: issue_occurrences id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.issue_occurrences ALTER COLUMN id SET DEFAULT nextval('public.issue_occurrences_id_seq'::regclass);


--
-- Name: issue_votes id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.issue_votes ALTER COLUMN id SET DEFAULT nextval('public.issue_votes_id_seq'::regclass);


--
-- Name: setups id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.setups ALTER COLUMN id SET DEFAULT nextval('public.setups_id_seq'::regclass);


--
-- Name: user_vehicles id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_vehicles ALTER COLUMN id SET DEFAULT nextval('public.user_vehicles_id_seq'::regclass);


--
-- Name: users id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);


--
-- Name: vehicle_images id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.vehicle_images ALTER COLUMN id SET DEFAULT nextval('public.vehicle_images_id_seq'::regclass);


--
-- Name: vehicle_models id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.vehicle_models ALTER COLUMN id SET DEFAULT nextval('public.vehicle_models_id_seq'::regclass);


--
-- Name: chronic_issues chronic_issues_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.chronic_issues
    ADD CONSTRAINT chronic_issues_pkey PRIMARY KEY (id);


--
-- Name: current_setups current_setups_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.current_setups
    ADD CONSTRAINT current_setups_pkey PRIMARY KEY (id);


--
-- Name: issue_occurrences issue_occurrences_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.issue_occurrences
    ADD CONSTRAINT issue_occurrences_pkey PRIMARY KEY (id);


--
-- Name: issue_votes issue_votes_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.issue_votes
    ADD CONSTRAINT issue_votes_pkey PRIMARY KEY (id);


--
-- Name: setups setups_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.setups
    ADD CONSTRAINT setups_pkey PRIMARY KEY (id);


--
-- Name: issue_occurrences uk4ti8rnrghutche0eqghfoaqcd; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.issue_occurrences
    ADD CONSTRAINT uk4ti8rnrghutche0eqghfoaqcd UNIQUE (issue_id, user_vehicle_id);


--
-- Name: users uk_6dotkott2kjsp8vw4d0m25fb7; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT uk_6dotkott2kjsp8vw4d0m25fb7 UNIQUE (email);


--
-- Name: issue_votes ukplyb9h0cqhrd7mkygodtmhdtp; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.issue_votes
    ADD CONSTRAINT ukplyb9h0cqhrd7mkygodtmhdtp UNIQUE (issue_id, user_id);


--
-- Name: user_vehicles user_vehicles_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_vehicles
    ADD CONSTRAINT user_vehicles_pkey PRIMARY KEY (id);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: vehicle_images vehicle_images_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.vehicle_images
    ADD CONSTRAINT vehicle_images_pkey PRIMARY KEY (id);


--
-- Name: vehicle_models vehicle_models_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.vehicle_models
    ADD CONSTRAINT vehicle_models_pkey PRIMARY KEY (id);


--
-- Name: unique_primary_image_per_vehicle; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX unique_primary_image_per_vehicle ON public.vehicle_images USING btree (user_vehicle_id) WHERE (primary_image = true);


--
-- Name: issue_occurrences fk12ytp77irqmitx1u0lhrp3g3v; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.issue_occurrences
    ADD CONSTRAINT fk12ytp77irqmitx1u0lhrp3g3v FOREIGN KEY (user_vehicle_id) REFERENCES public.user_vehicles(id);


--
-- Name: vehicle_images fk3cfi4dg35q6rq4f4ixojru6x5; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.vehicle_images
    ADD CONSTRAINT fk3cfi4dg35q6rq4f4ixojru6x5 FOREIGN KEY (user_vehicle_id) REFERENCES public.user_vehicles(id);


--
-- Name: current_setups fk3kh60o0ja4igq46c15u70g2l9; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.current_setups
    ADD CONSTRAINT fk3kh60o0ja4igq46c15u70g2l9 FOREIGN KEY (user_vehicle_id) REFERENCES public.user_vehicles(id);


--
-- Name: chronic_issue_preventive_maintenance fk85fck6tj10uvdvnkc7dmdwuc4; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.chronic_issue_preventive_maintenance
    ADD CONSTRAINT fk85fck6tj10uvdvnkc7dmdwuc4 FOREIGN KEY (chronic_issues_id) REFERENCES public.chronic_issues(id);


--
-- Name: issue_votes fkasg8nguv73rfk7q3vdayl2oke; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.issue_votes
    ADD CONSTRAINT fkasg8nguv73rfk7q3vdayl2oke FOREIGN KEY (issue_id) REFERENCES public.chronic_issues(id);


--
-- Name: setups fkdm3ycn6t0twfg0ub8k10xq19a; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.setups
    ADD CONSTRAINT fkdm3ycn6t0twfg0ub8k10xq19a FOREIGN KEY (vehicle_catalog_model_id) REFERENCES public.vehicle_models(id);


--
-- Name: current_setups fkfuulqr026v6fmndyh540q5hja; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.current_setups
    ADD CONSTRAINT fkfuulqr026v6fmndyh540q5hja FOREIGN KEY (based_on_id) REFERENCES public.setups(id);


--
-- Name: issue_occurrences fkj4vrwabjddlowm3pwd4qe48tn; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.issue_occurrences
    ADD CONSTRAINT fkj4vrwabjddlowm3pwd4qe48tn FOREIGN KEY (issue_id) REFERENCES public.chronic_issues(id);


--
-- Name: chronic_issues fkl4tw8akro93naea4daeyrsf3g; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.chronic_issues
    ADD CONSTRAINT fkl4tw8akro93naea4daeyrsf3g FOREIGN KEY (vehicle_catalog_model_id) REFERENCES public.vehicle_models(id);


--
-- Name: chronic_issues_symptoms fknaxf3si9aw5ltf5tvb4rlmrql; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.chronic_issues_symptoms
    ADD CONSTRAINT fknaxf3si9aw5ltf5tvb4rlmrql FOREIGN KEY (chronic_issues_id) REFERENCES public.chronic_issues(id);


--
-- Name: user_vehicles fkq9l9j4b3arq8woctei0esuc5i; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_vehicles
    ADD CONSTRAINT fkq9l9j4b3arq8woctei0esuc5i FOREIGN KEY (vehicle_catalog_model_id) REFERENCES public.vehicle_models(id);


--
-- PostgreSQL database dump complete
--