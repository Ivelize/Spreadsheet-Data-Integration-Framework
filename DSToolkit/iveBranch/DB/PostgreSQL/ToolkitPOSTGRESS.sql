toc.dat                                                                                             100600  004000  002000  00000355247 12511735575 007335  0                                                                                                    ustar00                                                                                                                                                                                                                                                        PGDMP                    
        s           dataspacesMondial    9.1.14    9.1.14 2   c	           0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                       false         d	           0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                       false         e	           1262    16393    dataspacesMondial    DATABASE     �   CREATE DATABASE "dataspacesMondial" WITH TEMPLATE = template0 ENCODING = 'UTF8' LC_COLLATE = 'English_United Kingdom.1252' LC_CTYPE = 'English_United Kingdom.1252';
 #   DROP DATABASE "dataspacesMondial";
             postgres    false                     2615    2200    public    SCHEMA        CREATE SCHEMA public;
    DROP SCHEMA public;
             postgres    false         f	           0    0    SCHEMA public    COMMENT     6   COMMENT ON SCHEMA public IS 'standard public schema';
                  postgres    false    6         g	           0    0    public    ACL     �   REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;
                  postgres    false    6         �            3079    11639    plpgsql 	   EXTENSION     ?   CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;
    DROP EXTENSION plpgsql;
                  false         h	           0    0    EXTENSION plpgsql    COMMENT     @   COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';
                       false    220         �            1259    228651    annotation_annotated_constructs    TABLE     �   CREATE TABLE annotation_annotated_constructs (
    annotation_id bigint NOT NULL,
    annotated_construct_id bigint NOT NULL
);
 3   DROP TABLE public.annotation_annotated_constructs;
       public         postgres    false    6         �            1259    228656 "   annotation_constraining_constructs    TABLE     �   CREATE TABLE annotation_constraining_constructs (
    annotation_id bigint NOT NULL,
    constraining_construct_id bigint NOT NULL
);
 6   DROP TABLE public.annotation_constraining_constructs;
       public         postgres    false    6         �            1259    228646    annotations    TABLE     �   CREATE TABLE annotations (
    id bigint NOT NULL,
    obj_version integer,
    "timestamp" time without time zone,
    annotation_value character varying(255) NOT NULL,
    annotation_ontology_term_id bigint,
    annotation_user_id bigint
);
    DROP TABLE public.annotations;
       public         postgres    false    6         �            1259    228661    constructs_morphisms    TABLE     i   CREATE TABLE constructs_morphisms (
    construct_id bigint NOT NULL,
    morphism_id bigint NOT NULL
);
 (   DROP TABLE public.constructs_morphisms;
       public         postgres    false    6         �            1259    228666    datasources    TABLE       CREATE TABLE datasources (
    id bigint NOT NULL,
    obj_version integer,
    construct_is_user_specified boolean,
    mmc_dataspace_id bigint,
    connection_url character varying(255) NOT NULL,
    description character varying(255),
    driver_class character varying(255),
    is_rdf character varying(255),
    data_source_name character varying(255),
    password character varying(255),
    schema_url character varying(255),
    sparql_url character varying(255),
    user_name character varying(255)
);
    DROP TABLE public.datasources;
       public         postgres    false    6         �            1259    228679    dataspace_datasources    TABLE     l   CREATE TABLE dataspace_datasources (
    dataspace_id bigint NOT NULL,
    datasource_id bigint NOT NULL
);
 )   DROP TABLE public.dataspace_datasources;
       public         postgres    false    6         �            1259    228684    dataspace_schemas    TABLE     d   CREATE TABLE dataspace_schemas (
    dataspace_id bigint NOT NULL,
    schema_id bigint NOT NULL
);
 %   DROP TABLE public.dataspace_schemas;
       public         postgres    false    6         �            1259    228689    dataspace_users    TABLE     `   CREATE TABLE dataspace_users (
    dataspace_id bigint NOT NULL,
    user_id bigint NOT NULL
);
 #   DROP TABLE public.dataspace_users;
       public         postgres    false    6         �            1259    228674 
   dataspaces    TABLE     x   CREATE TABLE dataspaces (
    id bigint NOT NULL,
    obj_version integer,
    dataspace_name character varying(255)
);
    DROP TABLE public.dataspaces;
       public         postgres    false    6         �            1259    229612    hibernate_sequence    SEQUENCE     t   CREATE SEQUENCE hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 )   DROP SEQUENCE public.hibernate_sequence;
       public       postgres    false    6         �            1259    228694    joinoperator    TABLE       CREATE TABLE joinoperator (
    id bigint NOT NULL,
    obj_version integer,
    construct_is_user_specified boolean,
    mmc_dataspace_id bigint,
    mapping_operator_and_or character varying(255),
    mapping_operator_variable_name character varying(255),
    mapping_operator_data_source_id bigint,
    mapping_operator_lhs_input_operator_id bigint,
    mapping_operator_mapping_id bigint,
    reconcilingexpression_id bigint,
    mapping_operator_result_type_id bigint,
    mapping_operator_rhs_input_operator_id bigint
);
     DROP TABLE public.joinoperator;
       public         postgres    false    6         �            1259    228702    kernel_density_estimators    TABLE     R  CREATE TABLE kernel_density_estimators (
    id bigint NOT NULL,
    obj_version integer,
    estimator_name character varying(255) NOT NULL,
    kernel_smoothing double precision,
    estimator_is_bounded boolean,
    case_type character varying(255),
    estimator_type character varying(255),
    kernel_type character varying(255)
);
 -   DROP TABLE public.kernel_density_estimators;
       public         postgres    false    6         �            1259    228710    mappings    TABLE     `  CREATE TABLE mappings (
    id bigint NOT NULL,
    obj_version integer,
    construct_is_user_specified boolean,
    mmc_dataspace_id bigint,
    cardinality_type character varying(255),
    mapping_query1_string character varying(1000),
    mapping_query2_string character varying(1000),
    mapping_query1_id bigint,
    mapping_query2_id bigint
);
    DROP TABLE public.mappings;
       public         postgres    false    6         �            1259    228718    morphism_constructs1    TABLE     j   CREATE TABLE morphism_constructs1 (
    morphism_id bigint NOT NULL,
    construct1_id bigint NOT NULL
);
 (   DROP TABLE public.morphism_constructs1;
       public         postgres    false    6         �            1259    228723    morphism_constructs2    TABLE     j   CREATE TABLE morphism_constructs2 (
    morphism_id bigint NOT NULL,
    construct2_id bigint NOT NULL
);
 (   DROP TABLE public.morphism_constructs2;
       public         postgres    false    6         �            1259    228728    one_to_one_matchings    TABLE     �  CREATE TABLE one_to_one_matchings (
    id bigint NOT NULL,
    obj_version integer,
    construct_is_user_specified boolean,
    mmc_dataspace_id bigint,
    cardinality_type character varying(255),
    abs_error double precision,
    class_label character varying(255),
    matcher_name character varying(255),
    matching_score double precision,
    squared_error double precision,
    parent_match_id bigint,
    matching_construct1_id bigint,
    matching_construct2_id bigint
);
 (   DROP TABLE public.one_to_one_matchings;
       public         postgres    false    6         �            1259    228744    ontology_term_enum_values    TABLE     o   CREATE TABLE ontology_term_enum_values (
    enum_id bigint NOT NULL,
    enum_value character varying(255)
);
 -   DROP TABLE public.ontology_term_enum_values;
       public         postgres    false    6         �            1259    228736    ontology_terms    TABLE     �   CREATE TABLE ontology_terms (
    id bigint NOT NULL,
    obj_version integer,
    ontology_term_data_type character varying(255),
    ontology_term_name character varying(255) NOT NULL,
    parent_ontology_term_id bigint
);
 "   DROP TABLE public.ontology_terms;
       public         postgres    false    6         �            1259    228755    parameter_applied_to_construct    TABLE     t   CREATE TABLE parameter_applied_to_construct (
    parameter_id bigint NOT NULL,
    construct_id bigint NOT NULL
);
 2   DROP TABLE public.parameter_applied_to_construct;
       public         postgres    false    6         �            1259    228747 
   parameters    TABLE        CREATE TABLE parameters (
    id bigint NOT NULL,
    obj_version integer,
    parameter_direction character varying(255) NOT NULL,
    parameter_name character varying(255) NOT NULL,
    parameter_value character varying(255) NOT NULL,
    schematic_correspondence_id bigint NOT NULL
);
    DROP TABLE public.parameters;
       public         postgres    false    6         �            1259    228760 *   participation_of_cmc_in_super_relationship    TABLE     2  CREATE TABLE participation_of_cmc_in_super_relationship (
    id bigint NOT NULL,
    obj_version integer,
    construct_is_user_specified boolean,
    mmc_dataspace_id bigint,
    role_of_construct_in_super_relationship character varying(255),
    construct_id bigint,
    super_relationship_id bigint
);
 >   DROP TABLE public.participation_of_cmc_in_super_relationship;
       public         postgres    false    6         �            1259    228765 &   participation_specifying_super_lexical    TABLE     �   CREATE TABLE participation_specifying_super_lexical (
    super_lexical_id bigint NOT NULL,
    participation_in_super_relationship_id bigint NOT NULL
);
 :   DROP TABLE public.participation_specifying_super_lexical;
       public         postgres    false    6         �            1259    228770 
   predicates    TABLE     �  CREATE TABLE predicates (
    id bigint NOT NULL,
    obj_version integer,
    construct_is_user_specified boolean,
    mmc_dataspace_id bigint,
    andor character varying(255),
    literal1 character varying(255),
    literal2 character varying(255),
    operator character varying(255),
    predicate_super_lexical1_id bigint,
    predicate_super_lexical2_id bigint,
    scan_predicate_id bigint,
    join_predicate_id bigint
);
    DROP TABLE public.predicates;
       public         postgres    false    6         �            1259    228778 
   properties    TABLE     .  CREATE TABLE properties (
    id bigint NOT NULL,
    obj_version integer,
    property_data_type character varying(255),
    property_lang character varying(255),
    property_name character varying(255) NOT NULL,
    property_value character varying(255) NOT NULL,
    property_id bigint NOT NULL
);
    DROP TABLE public.properties;
       public         postgres    false    6         �            1259    228786    queries    TABLE     k  CREATE TABLE queries (
    id bigint NOT NULL,
    obj_version integer,
    construct_is_user_specified boolean,
    mmc_dataspace_id bigint,
    query_description character varying(255),
    query_name character varying(255),
    query_string character varying(1000),
    query_dataspace_id bigint,
    query_root_operator_id bigint,
    query_user_id bigint
);
    DROP TABLE public.queries;
       public         postgres    false    6         �            1259    228794    query_datasources    TABLE     d   CREATE TABLE query_datasources (
    query_id bigint NOT NULL,
    datasource_id bigint NOT NULL
);
 %   DROP TABLE public.query_datasources;
       public         postgres    false    6         �            1259    228804    query_result_mapping    TABLE     k   CREATE TABLE query_result_mapping (
    query_result_id bigint NOT NULL,
    mapping_id bigint NOT NULL
);
 (   DROP TABLE public.query_result_mapping;
       public         postgres    false    6         �            1259    228809    query_result_result_instance    TABLE     {   CREATE TABLE query_result_result_instance (
    query_result_id bigint NOT NULL,
    result_instance_id bigint NOT NULL
);
 0   DROP TABLE public.query_result_result_instance;
       public         postgres    false    6         �            1259    228812 "   query_result_schema_of_data_source    TABLE     �   CREATE TABLE query_result_schema_of_data_source (
    query_result_id bigint NOT NULL,
    schema_of_data_source_id bigint NOT NULL
);
 6   DROP TABLE public.query_result_schema_of_data_source;
       public         postgres    false    6         �            1259    228799    query_results    TABLE       CREATE TABLE query_results (
    id bigint NOT NULL,
    obj_version integer,
    construct_is_user_specified boolean,
    mmc_dataspace_id bigint,
    query_result_dataspace_id bigint,
    query_result_query_id bigint,
    query_result_result_type_id bigint
);
 !   DROP TABLE public.query_results;
       public         postgres    false    6         �            1259    228817    query_schemas    TABLE     \   CREATE TABLE query_schemas (
    query_id bigint NOT NULL,
    schema_id bigint NOT NULL
);
 !   DROP TABLE public.query_schemas;
       public         postgres    false    6         �            1259    228822    query_super_abstracts    TABLE     l   CREATE TABLE query_super_abstracts (
    query_id bigint NOT NULL,
    super_abstract_id bigint NOT NULL
);
 )   DROP TABLE public.query_super_abstracts;
       public         postgres    false    6         �            1259    228827    reconciling_expression    TABLE     �  CREATE TABLE reconciling_expression (
    id bigint NOT NULL,
    obj_version integer,
    reconciling_expression_expression character varying(10000),
    reconciling_expression_type character varying(255),
    reconciling_expression_applied_to_canonical_model_construct bigint,
    reconciling_expression_join_pred_1 bigint,
    reconciling_expression_join_pred_2 bigint,
    reconciling_expression_selection_target_super_abstract bigint
);
 *   DROP TABLE public.reconciling_expression;
       public         postgres    false    6         �            1259    228888    reduceoperator    TABLE       CREATE TABLE reduceoperator (
    id bigint NOT NULL,
    obj_version integer,
    construct_is_user_specified boolean,
    mmc_dataspace_id bigint,
    mapping_operator_and_or character varying(255),
    mapping_operator_variable_name character varying(255),
    mapping_operator_data_source_id bigint,
    mapping_operator_lhs_input_operator_id bigint,
    mapping_operator_mapping_id bigint,
    reconcilingexpression_id bigint,
    mapping_operator_result_type_id bigint,
    mapping_operator_rhs_input_operator_id bigint
);
 "   DROP TABLE public.reduceoperator;
       public         postgres    false    6         �            1259    228896    renameoperator    TABLE     m  CREATE TABLE renameoperator (
    id bigint NOT NULL,
    obj_version integer,
    construct_is_user_specified boolean,
    mmc_dataspace_id bigint,
    mapping_operator_and_or character varying(255),
    mapping_operator_variable_name character varying(255),
    mapping_operator_data_source_id bigint,
    mapping_operator_lhs_input_operator_id bigint,
    mapping_operator_mapping_id bigint,
    reconcilingexpression_id bigint,
    mapping_operator_result_type_id bigint,
    mapping_operator_rhs_input_operator_id bigint,
    rename_new_name character varying(255),
    rename_canonical_model_construct_id bigint
);
 "   DROP TABLE public.renameoperator;
       public         postgres    false    6         �            1259    228843    result_field_result_values    TABLE       CREATE TABLE result_field_result_values (
    id bigint NOT NULL,
    obj_version integer,
    construct_is_user_specified boolean,
    mmc_dataspace_id bigint,
    result_field_name character varying(10000),
    result_value_value character varying(50000)
);
 .   DROP TABLE public.result_field_result_values;
       public         postgres    false    6         �            1259    228835    result_fields    TABLE     �   CREATE TABLE result_fields (
    id bigint NOT NULL,
    obj_version integer,
    result_field_name character varying(255),
    result_field_type character varying(255),
    result_field_index integer,
    construct_id bigint
);
 !   DROP TABLE public.result_fields;
       public         postgres    false    6         �            1259    228856    result_instance_mapping    TABLE     q   CREATE TABLE result_instance_mapping (
    result_instance_id bigint NOT NULL,
    mapping_id bigint NOT NULL
);
 +   DROP TABLE public.result_instance_mapping;
       public         postgres    false    6         �            1259    228861    result_instance_result_values    TABLE     �   CREATE TABLE result_instance_result_values (
    result_instance_id bigint NOT NULL,
    result_field_result_values_id bigint NOT NULL
);
 1   DROP TABLE public.result_instance_result_values;
       public         postgres    false    6         �            1259    228851    result_instances    TABLE     �   CREATE TABLE result_instances (
    id bigint NOT NULL,
    obj_version integer,
    construct_is_user_specified boolean,
    mmc_dataspace_id bigint,
    result_instance_query_id bigint,
    result_type_id bigint
);
 $   DROP TABLE public.result_instances;
       public         postgres    false    6         �            1259    228871    result_type_result_fields    TABLE     t   CREATE TABLE result_type_result_fields (
    result_type_id bigint NOT NULL,
    result_field_id bigint NOT NULL
);
 -   DROP TABLE public.result_type_result_fields;
       public         postgres    false    6         �            1259    228866    result_types    TABLE     O   CREATE TABLE result_types (
    id bigint NOT NULL,
    obj_version integer
);
     DROP TABLE public.result_types;
       public         postgres    false    6         �            1259    228883 
   role_users    TABLE     V   CREATE TABLE role_users (
    role_id bigint NOT NULL,
    user_id bigint NOT NULL
);
    DROP TABLE public.role_users;
       public         postgres    false    6         �            1259    228878    roles    TABLE     s   CREATE TABLE roles (
    id bigint NOT NULL,
    obj_version integer,
    role_role_type character varying(255)
);
    DROP TABLE public.roles;
       public         postgres    false    6         �            1259    228904 
   sample_kde    TABLE     �   CREATE TABLE sample_kde (
    id bigint NOT NULL,
    obj_version integer,
    sample_value double precision NOT NULL,
    estimator_id bigint NOT NULL
);
    DROP TABLE public.sample_kde;
       public         postgres    false    6         �            1259    228973    scanoperator    TABLE     2  CREATE TABLE scanoperator (
    id bigint NOT NULL,
    obj_version integer,
    construct_is_user_specified boolean,
    mmc_dataspace_id bigint,
    mapping_operator_and_or character varying(255),
    mapping_operator_variable_name character varying(255),
    mapping_operator_data_source_id bigint,
    mapping_operator_lhs_input_operator_id bigint,
    mapping_operator_mapping_id bigint,
    reconcilingexpression_id bigint,
    mapping_operator_result_type_id bigint,
    mapping_operator_rhs_input_operator_id bigint,
    scan_super_abstract_id bigint
);
     DROP TABLE public.scanoperator;
       public         postgres    false    6         �            1259    228909    schemas    TABLE       CREATE TABLE schemas (
    id bigint NOT NULL,
    obj_version integer,
    construct_is_user_specified boolean,
    mmc_dataspace_id bigint,
    model_type character varying(255),
    schema_name character varying(255) NOT NULL,
    data_source_id bigint
);
    DROP TABLE public.schemas;
       public         postgres    false    6         �            1259    228935 2   schematic_correspondence_reconciling_expressions_1    TABLE     �   CREATE TABLE schematic_correspondence_reconciling_expressions_1 (
    schematic_correspondence_id bigint NOT NULL,
    reconciling_expression_id bigint NOT NULL
);
 F   DROP TABLE public.schematic_correspondence_reconciling_expressions_1;
       public         postgres    false    6         �            1259    228942 2   schematic_correspondence_reconciling_expressions_2    TABLE     �   CREATE TABLE schematic_correspondence_reconciling_expressions_2 (
    schematic_correspondence_id bigint NOT NULL,
    reconciling_expression_id bigint NOT NULL
);
 F   DROP TABLE public.schematic_correspondence_reconciling_expressions_2;
       public         postgres    false    6         �            1259    228917    schematic_correspondences    TABLE     x  CREATE TABLE schematic_correspondences (
    id bigint NOT NULL,
    obj_version integer,
    construct_is_user_specified boolean,
    mmc_dataspace_id bigint,
    cardinality_type character varying(255),
    construct_related_schematic_correspondence_type character varying(255),
    schematic_correspondence_description character varying(255),
    parameter_direction character varying(255) NOT NULL,
    schematic_correspondence_name character varying(255),
    schematic_correspondence_type character varying(255),
    schematic_correspondence_short_name character varying(255),
    parent_schematic_correspondence_id bigint
);
 -   DROP TABLE public.schematic_correspondences;
       public         postgres    false    6         �            1259    228925 /   schematic_correspondences_to_mapping_provenance    TABLE     �   CREATE TABLE schematic_correspondences_to_mapping_provenance (
    id bigint NOT NULL,
    obj_version integer,
    mapping_id bigint
);
 C   DROP TABLE public.schematic_correspondences_to_mapping_provenance;
       public         postgres    false    6         �            1259    228930 :   schematic_correspondences_utilised_for_mappings_provenance    TABLE     �   CREATE TABLE schematic_correspondences_utilised_for_mappings_provenance (
    provenance_id bigint NOT NULL,
    schematic_correspondence_id bigint NOT NULL
);
 N   DROP TABLE public.schematic_correspondences_utilised_for_mappings_provenance;
       public         postgres    false    6         �            1259    228981    setoperator    TABLE     5  CREATE TABLE setoperator (
    id bigint NOT NULL,
    obj_version integer,
    construct_is_user_specified boolean,
    mmc_dataspace_id bigint,
    mapping_operator_and_or character varying(255),
    mapping_operator_variable_name character varying(255),
    mapping_operator_data_source_id bigint,
    mapping_operator_lhs_input_operator_id bigint,
    mapping_operator_mapping_id bigint,
    reconcilingexpression_id bigint,
    mapping_operator_result_type_id bigint,
    mapping_operator_rhs_input_operator_id bigint,
    setop_type character varying(255)
);
    DROP TABLE public.setoperator;
       public         postgres    false    6         �            1259    228949    super_abstracts    TABLE     x  CREATE TABLE super_abstracts (
    id bigint NOT NULL,
    obj_version integer,
    construct_is_user_specified boolean,
    mmc_dataspace_id bigint,
    canonical_model_construct_is_global boolean,
    canonical_model_construct_is_virtual boolean,
    canonical_model_construct_name character varying(255) NOT NULL,
    canonical_model_construct_type character varying(255),
    canonical_model_construct_schema_id bigint,
    super_abstract_cardinality integer,
    super_abstract_midst_super_model_type character varying(255),
    super_abstract_model_specific_type character varying(255),
    parent_super_abstract_id bigint
);
 #   DROP TABLE public.super_abstracts;
       public         postgres    false    6         �            1259    228957    super_lexicals    TABLE     �  CREATE TABLE super_lexicals (
    id bigint NOT NULL,
    obj_version integer,
    construct_is_user_specified boolean,
    mmc_dataspace_id bigint,
    canonical_model_construct_is_global boolean,
    canonical_model_construct_is_virtual boolean,
    canonical_model_construct_name character varying(255) NOT NULL,
    canonical_model_construct_type character varying(255),
    canonical_model_construct_schema_id bigint,
    super_lexical_data_type character varying(255),
    super_lexical_is_identifier boolean,
    super_lexical_is_nullable boolean,
    super_lexical_is_optional boolean,
    super_lexical_max_value_size integer,
    super_lexical_midst_super_model_type character varying(255),
    super_lexical_model_specific_type character varying(255),
    super_lexical_number_of_distinct_values integer,
    super_lexical_parent_super_abstract_id bigint,
    parent_super_lexical_id bigint,
    parent_super_relationship_id bigint,
    super_lexical_id bigint,
    mapkey character varying(255)
);
 "   DROP TABLE public.super_lexicals;
       public         postgres    false    6         �            1259    228965    super_relationships    TABLE     �  CREATE TABLE super_relationships (
    id bigint NOT NULL,
    obj_version integer,
    construct_is_user_specified boolean,
    mmc_dataspace_id bigint,
    canonical_model_construct_is_global boolean,
    canonical_model_construct_is_virtual boolean,
    canonical_model_construct_name character varying(255) NOT NULL,
    canonical_model_construct_type character varying(255),
    canonical_model_construct_schema_id bigint,
    super_relationship_midst_super_model_type character varying(255),
    super_relationship_model_specific_type character varying(255),
    super_relationship_type character varying(255),
    super_relationship_generalised_super_abstract_id bigint
);
 '   DROP TABLE public.super_relationships;
       public         postgres    false    6         �            1259    228989    temp_construct    TABLE     �  CREATE TABLE temp_construct (
    id bigint NOT NULL,
    obj_version integer,
    construct_is_user_specified boolean,
    mmc_dataspace_id bigint,
    canonical_model_construct_is_global boolean,
    canonical_model_construct_is_virtual boolean,
    canonical_model_construct_name character varying(255) NOT NULL,
    canonical_model_construct_type character varying(255),
    canonical_model_construct_schema_id bigint
);
 "   DROP TABLE public.temp_construct;
       public         postgres    false    6         �            1259    228997    typecastoperator    TABLE     u  CREATE TABLE typecastoperator (
    id bigint NOT NULL,
    obj_version integer,
    construct_is_user_specified boolean,
    mmc_dataspace_id bigint,
    mapping_operator_and_or character varying(255),
    mapping_operator_variable_name character varying(255),
    mapping_operator_data_source_id bigint,
    mapping_operator_lhs_input_operator_id bigint,
    mapping_operator_mapping_id bigint,
    reconcilingexpression_id bigint,
    mapping_operator_result_type_id bigint,
    mapping_operator_rhs_input_operator_id bigint,
    type_cast_new_type character varying(255),
    type_cast_canonical_model_construct_id bigint
);
 $   DROP TABLE public.typecastoperator;
       public         postgres    false    6         �            1259    229005    users    TABLE     �  CREATE TABLE users (
    id bigint NOT NULL,
    obj_version integer,
    accept_terms boolean,
    date_created timestamp without time zone,
    email character varying(255) NOT NULL,
    first_name character varying(255),
    institution_name character varying(255),
    last_name character varying(255),
    password character varying(255) NOT NULL,
    user_name character varying(255) NOT NULL
);
    DROP TABLE public.users;
       public         postgres    false    6         '	          0    228651    annotation_annotated_constructs 
   TABLE DATA               Y   COPY annotation_annotated_constructs (annotation_id, annotated_construct_id) FROM stdin;
    public       postgres    false    162    2401       2343.dat (	          0    228656 "   annotation_constraining_constructs 
   TABLE DATA               _   COPY annotation_constraining_constructs (annotation_id, constraining_construct_id) FROM stdin;
    public       postgres    false    163    2401       2344.dat &	          0    228646    annotations 
   TABLE DATA                  COPY annotations (id, obj_version, "timestamp", annotation_value, annotation_ontology_term_id, annotation_user_id) FROM stdin;
    public       postgres    false    161    2401       2342.dat )	          0    228661    constructs_morphisms 
   TABLE DATA               B   COPY constructs_morphisms (construct_id, morphism_id) FROM stdin;
    public       postgres    false    164    2401       2345.dat *	          0    228666    datasources 
   TABLE DATA               �   COPY datasources (id, obj_version, construct_is_user_specified, mmc_dataspace_id, connection_url, description, driver_class, is_rdf, data_source_name, password, schema_url, sparql_url, user_name) FROM stdin;
    public       postgres    false    165    2401       2346.dat ,	          0    228679    dataspace_datasources 
   TABLE DATA               E   COPY dataspace_datasources (dataspace_id, datasource_id) FROM stdin;
    public       postgres    false    167    2401       2348.dat -	          0    228684    dataspace_schemas 
   TABLE DATA               =   COPY dataspace_schemas (dataspace_id, schema_id) FROM stdin;
    public       postgres    false    168    2401       2349.dat .	          0    228689    dataspace_users 
   TABLE DATA               9   COPY dataspace_users (dataspace_id, user_id) FROM stdin;
    public       postgres    false    169    2401       2350.dat +	          0    228674 
   dataspaces 
   TABLE DATA               >   COPY dataspaces (id, obj_version, dataspace_name) FROM stdin;
    public       postgres    false    166    2401       2347.dat i	           0    0    hibernate_sequence    SEQUENCE SET     ;   SELECT pg_catalog.setval('hibernate_sequence', 868, true);
            public       postgres    false    219         /	          0    228694    joinoperator 
   TABLE DATA               a  COPY joinoperator (id, obj_version, construct_is_user_specified, mmc_dataspace_id, mapping_operator_and_or, mapping_operator_variable_name, mapping_operator_data_source_id, mapping_operator_lhs_input_operator_id, mapping_operator_mapping_id, reconcilingexpression_id, mapping_operator_result_type_id, mapping_operator_rhs_input_operator_id) FROM stdin;
    public       postgres    false    170    2401       2351.dat 0	          0    228702    kernel_density_estimators 
   TABLE DATA               �   COPY kernel_density_estimators (id, obj_version, estimator_name, kernel_smoothing, estimator_is_bounded, case_type, estimator_type, kernel_type) FROM stdin;
    public       postgres    false    171    2401       2352.dat 1	          0    228710    mappings 
   TABLE DATA               �   COPY mappings (id, obj_version, construct_is_user_specified, mmc_dataspace_id, cardinality_type, mapping_query1_string, mapping_query2_string, mapping_query1_id, mapping_query2_id) FROM stdin;
    public       postgres    false    172    2401       2353.dat 2	          0    228718    morphism_constructs1 
   TABLE DATA               C   COPY morphism_constructs1 (morphism_id, construct1_id) FROM stdin;
    public       postgres    false    173    2401       2354.dat 3	          0    228723    morphism_constructs2 
   TABLE DATA               C   COPY morphism_constructs2 (morphism_id, construct2_id) FROM stdin;
    public       postgres    false    174    2401       2355.dat 4	          0    228728    one_to_one_matchings 
   TABLE DATA               �   COPY one_to_one_matchings (id, obj_version, construct_is_user_specified, mmc_dataspace_id, cardinality_type, abs_error, class_label, matcher_name, matching_score, squared_error, parent_match_id, matching_construct1_id, matching_construct2_id) FROM stdin;
    public       postgres    false    175    2401       2356.dat 6	          0    228744    ontology_term_enum_values 
   TABLE DATA               A   COPY ontology_term_enum_values (enum_id, enum_value) FROM stdin;
    public       postgres    false    177    2401       2358.dat 5	          0    228736    ontology_terms 
   TABLE DATA               x   COPY ontology_terms (id, obj_version, ontology_term_data_type, ontology_term_name, parent_ontology_term_id) FROM stdin;
    public       postgres    false    176    2401       2357.dat 8	          0    228755    parameter_applied_to_construct 
   TABLE DATA               M   COPY parameter_applied_to_construct (parameter_id, construct_id) FROM stdin;
    public       postgres    false    179    2401       2360.dat 7	          0    228747 
   parameters 
   TABLE DATA               �   COPY parameters (id, obj_version, parameter_direction, parameter_name, parameter_value, schematic_correspondence_id) FROM stdin;
    public       postgres    false    178    2401       2359.dat 9	          0    228760 *   participation_of_cmc_in_super_relationship 
   TABLE DATA               �   COPY participation_of_cmc_in_super_relationship (id, obj_version, construct_is_user_specified, mmc_dataspace_id, role_of_construct_in_super_relationship, construct_id, super_relationship_id) FROM stdin;
    public       postgres    false    180    2401       2361.dat :	          0    228765 &   participation_specifying_super_lexical 
   TABLE DATA               s   COPY participation_specifying_super_lexical (super_lexical_id, participation_in_super_relationship_id) FROM stdin;
    public       postgres    false    181    2401       2362.dat ;	          0    228770 
   predicates 
   TABLE DATA               �   COPY predicates (id, obj_version, construct_is_user_specified, mmc_dataspace_id, andor, literal1, literal2, operator, predicate_super_lexical1_id, predicate_super_lexical2_id, scan_predicate_id, join_predicate_id) FROM stdin;
    public       postgres    false    182    2401       2363.dat <	          0    228778 
   properties 
   TABLE DATA               }   COPY properties (id, obj_version, property_data_type, property_lang, property_name, property_value, property_id) FROM stdin;
    public       postgres    false    183    2401       2364.dat =	          0    228786    queries 
   TABLE DATA               �   COPY queries (id, obj_version, construct_is_user_specified, mmc_dataspace_id, query_description, query_name, query_string, query_dataspace_id, query_root_operator_id, query_user_id) FROM stdin;
    public       postgres    false    184    2401       2365.dat >	          0    228794    query_datasources 
   TABLE DATA               =   COPY query_datasources (query_id, datasource_id) FROM stdin;
    public       postgres    false    185    2401       2366.dat @	          0    228804    query_result_mapping 
   TABLE DATA               D   COPY query_result_mapping (query_result_id, mapping_id) FROM stdin;
    public       postgres    false    187    2401       2368.dat A	          0    228809    query_result_result_instance 
   TABLE DATA               T   COPY query_result_result_instance (query_result_id, result_instance_id) FROM stdin;
    public       postgres    false    188    2401       2369.dat B	          0    228812 "   query_result_schema_of_data_source 
   TABLE DATA               `   COPY query_result_schema_of_data_source (query_result_id, schema_of_data_source_id) FROM stdin;
    public       postgres    false    189    2401       2370.dat ?	          0    228799    query_results 
   TABLE DATA               �   COPY query_results (id, obj_version, construct_is_user_specified, mmc_dataspace_id, query_result_dataspace_id, query_result_query_id, query_result_result_type_id) FROM stdin;
    public       postgres    false    186    2401       2367.dat C	          0    228817    query_schemas 
   TABLE DATA               5   COPY query_schemas (query_id, schema_id) FROM stdin;
    public       postgres    false    190    2401       2371.dat D	          0    228822    query_super_abstracts 
   TABLE DATA               E   COPY query_super_abstracts (query_id, super_abstract_id) FROM stdin;
    public       postgres    false    191    2401       2372.dat E	          0    228827    reconciling_expression 
   TABLE DATA               7  COPY reconciling_expression (id, obj_version, reconciling_expression_expression, reconciling_expression_type, reconciling_expression_applied_to_canonical_model_construct, reconciling_expression_join_pred_1, reconciling_expression_join_pred_2, reconciling_expression_selection_target_super_abstract) FROM stdin;
    public       postgres    false    192    2401       2373.dat O	          0    228888    reduceoperator 
   TABLE DATA               c  COPY reduceoperator (id, obj_version, construct_is_user_specified, mmc_dataspace_id, mapping_operator_and_or, mapping_operator_variable_name, mapping_operator_data_source_id, mapping_operator_lhs_input_operator_id, mapping_operator_mapping_id, reconcilingexpression_id, mapping_operator_result_type_id, mapping_operator_rhs_input_operator_id) FROM stdin;
    public       postgres    false    202    2401       2383.dat P	          0    228896    renameoperator 
   TABLE DATA               �  COPY renameoperator (id, obj_version, construct_is_user_specified, mmc_dataspace_id, mapping_operator_and_or, mapping_operator_variable_name, mapping_operator_data_source_id, mapping_operator_lhs_input_operator_id, mapping_operator_mapping_id, reconcilingexpression_id, mapping_operator_result_type_id, mapping_operator_rhs_input_operator_id, rename_new_name, rename_canonical_model_construct_id) FROM stdin;
    public       postgres    false    203    2401       2384.dat G	          0    228843    result_field_result_values 
   TABLE DATA               �   COPY result_field_result_values (id, obj_version, construct_is_user_specified, mmc_dataspace_id, result_field_name, result_value_value) FROM stdin;
    public       postgres    false    194    2401       2375.dat F	          0    228835    result_fields 
   TABLE DATA               y   COPY result_fields (id, obj_version, result_field_name, result_field_type, result_field_index, construct_id) FROM stdin;
    public       postgres    false    193    2401       2374.dat I	          0    228856    result_instance_mapping 
   TABLE DATA               J   COPY result_instance_mapping (result_instance_id, mapping_id) FROM stdin;
    public       postgres    false    196    2401       2377.dat J	          0    228861    result_instance_result_values 
   TABLE DATA               c   COPY result_instance_result_values (result_instance_id, result_field_result_values_id) FROM stdin;
    public       postgres    false    197    2401       2378.dat H	          0    228851    result_instances 
   TABLE DATA               �   COPY result_instances (id, obj_version, construct_is_user_specified, mmc_dataspace_id, result_instance_query_id, result_type_id) FROM stdin;
    public       postgres    false    195    2401       2376.dat L	          0    228871    result_type_result_fields 
   TABLE DATA               M   COPY result_type_result_fields (result_type_id, result_field_id) FROM stdin;
    public       postgres    false    199    2401       2380.dat K	          0    228866    result_types 
   TABLE DATA               0   COPY result_types (id, obj_version) FROM stdin;
    public       postgres    false    198    2401       2379.dat N	          0    228883 
   role_users 
   TABLE DATA               /   COPY role_users (role_id, user_id) FROM stdin;
    public       postgres    false    201    2401       2382.dat M	          0    228878    roles 
   TABLE DATA               9   COPY roles (id, obj_version, role_role_type) FROM stdin;
    public       postgres    false    200    2401       2381.dat Q	          0    228904 
   sample_kde 
   TABLE DATA               J   COPY sample_kde (id, obj_version, sample_value, estimator_id) FROM stdin;
    public       postgres    false    204    2401       2385.dat [	          0    228973    scanoperator 
   TABLE DATA               y  COPY scanoperator (id, obj_version, construct_is_user_specified, mmc_dataspace_id, mapping_operator_and_or, mapping_operator_variable_name, mapping_operator_data_source_id, mapping_operator_lhs_input_operator_id, mapping_operator_mapping_id, reconcilingexpression_id, mapping_operator_result_type_id, mapping_operator_rhs_input_operator_id, scan_super_abstract_id) FROM stdin;
    public       postgres    false    214    2401       2395.dat R	          0    228909    schemas 
   TABLE DATA               �   COPY schemas (id, obj_version, construct_is_user_specified, mmc_dataspace_id, model_type, schema_name, data_source_id) FROM stdin;
    public       postgres    false    205    2401       2386.dat V	          0    228935 2   schematic_correspondence_reconciling_expressions_1 
   TABLE DATA               }   COPY schematic_correspondence_reconciling_expressions_1 (schematic_correspondence_id, reconciling_expression_id) FROM stdin;
    public       postgres    false    209    2401       2390.dat W	          0    228942 2   schematic_correspondence_reconciling_expressions_2 
   TABLE DATA               }   COPY schematic_correspondence_reconciling_expressions_2 (schematic_correspondence_id, reconciling_expression_id) FROM stdin;
    public       postgres    false    210    2401       2391.dat S	          0    228917    schematic_correspondences 
   TABLE DATA               q  COPY schematic_correspondences (id, obj_version, construct_is_user_specified, mmc_dataspace_id, cardinality_type, construct_related_schematic_correspondence_type, schematic_correspondence_description, parameter_direction, schematic_correspondence_name, schematic_correspondence_type, schematic_correspondence_short_name, parent_schematic_correspondence_id) FROM stdin;
    public       postgres    false    206    2401       2387.dat T	          0    228925 /   schematic_correspondences_to_mapping_provenance 
   TABLE DATA               _   COPY schematic_correspondences_to_mapping_provenance (id, obj_version, mapping_id) FROM stdin;
    public       postgres    false    207    2401       2388.dat U	          0    228930 :   schematic_correspondences_utilised_for_mappings_provenance 
   TABLE DATA               y   COPY schematic_correspondences_utilised_for_mappings_provenance (provenance_id, schematic_correspondence_id) FROM stdin;
    public       postgres    false    208    2401       2389.dat \	          0    228981    setoperator 
   TABLE DATA               l  COPY setoperator (id, obj_version, construct_is_user_specified, mmc_dataspace_id, mapping_operator_and_or, mapping_operator_variable_name, mapping_operator_data_source_id, mapping_operator_lhs_input_operator_id, mapping_operator_mapping_id, reconcilingexpression_id, mapping_operator_result_type_id, mapping_operator_rhs_input_operator_id, setop_type) FROM stdin;
    public       postgres    false    215    2401       2396.dat X	          0    228949    super_abstracts 
   TABLE DATA               �  COPY super_abstracts (id, obj_version, construct_is_user_specified, mmc_dataspace_id, canonical_model_construct_is_global, canonical_model_construct_is_virtual, canonical_model_construct_name, canonical_model_construct_type, canonical_model_construct_schema_id, super_abstract_cardinality, super_abstract_midst_super_model_type, super_abstract_model_specific_type, parent_super_abstract_id) FROM stdin;
    public       postgres    false    211    2401       2392.dat Y	          0    228957    super_lexicals 
   TABLE DATA               �  COPY super_lexicals (id, obj_version, construct_is_user_specified, mmc_dataspace_id, canonical_model_construct_is_global, canonical_model_construct_is_virtual, canonical_model_construct_name, canonical_model_construct_type, canonical_model_construct_schema_id, super_lexical_data_type, super_lexical_is_identifier, super_lexical_is_nullable, super_lexical_is_optional, super_lexical_max_value_size, super_lexical_midst_super_model_type, super_lexical_model_specific_type, super_lexical_number_of_distinct_values, super_lexical_parent_super_abstract_id, parent_super_lexical_id, parent_super_relationship_id, super_lexical_id, mapkey) FROM stdin;
    public       postgres    false    212    2401       2393.dat Z	          0    228965    super_relationships 
   TABLE DATA               �  COPY super_relationships (id, obj_version, construct_is_user_specified, mmc_dataspace_id, canonical_model_construct_is_global, canonical_model_construct_is_virtual, canonical_model_construct_name, canonical_model_construct_type, canonical_model_construct_schema_id, super_relationship_midst_super_model_type, super_relationship_model_specific_type, super_relationship_type, super_relationship_generalised_super_abstract_id) FROM stdin;
    public       postgres    false    213    2401       2394.dat ]	          0    228989    temp_construct 
   TABLE DATA                 COPY temp_construct (id, obj_version, construct_is_user_specified, mmc_dataspace_id, canonical_model_construct_is_global, canonical_model_construct_is_virtual, canonical_model_construct_name, canonical_model_construct_type, canonical_model_construct_schema_id) FROM stdin;
    public       postgres    false    216    2401       2397.dat ^	          0    228997    typecastoperator 
   TABLE DATA               �  COPY typecastoperator (id, obj_version, construct_is_user_specified, mmc_dataspace_id, mapping_operator_and_or, mapping_operator_variable_name, mapping_operator_data_source_id, mapping_operator_lhs_input_operator_id, mapping_operator_mapping_id, reconcilingexpression_id, mapping_operator_result_type_id, mapping_operator_rhs_input_operator_id, type_cast_new_type, type_cast_canonical_model_construct_id) FROM stdin;
    public       postgres    false    217    2401       2398.dat _	          0    229005    users 
   TABLE DATA               �   COPY users (id, obj_version, accept_terms, date_created, email, first_name, institution_name, last_name, password, user_name) FROM stdin;
    public       postgres    false    218    2401       2399.dat �           2606    228655 $   annotation_annotated_constructs_pkey 
   CONSTRAINT     �   ALTER TABLE ONLY annotation_annotated_constructs
    ADD CONSTRAINT annotation_annotated_constructs_pkey PRIMARY KEY (annotation_id, annotated_construct_id);
 n   ALTER TABLE ONLY public.annotation_annotated_constructs DROP CONSTRAINT annotation_annotated_constructs_pkey;
       public         postgres    false    162    162    162    2402         �           2606    228660 '   annotation_constraining_constructs_pkey 
   CONSTRAINT     �   ALTER TABLE ONLY annotation_constraining_constructs
    ADD CONSTRAINT annotation_constraining_constructs_pkey PRIMARY KEY (annotation_id, constraining_construct_id);
 t   ALTER TABLE ONLY public.annotation_constraining_constructs DROP CONSTRAINT annotation_constraining_constructs_pkey;
       public         postgres    false    163    163    163    2402         �           2606    228650    annotations_pkey 
   CONSTRAINT     S   ALTER TABLE ONLY annotations
    ADD CONSTRAINT annotations_pkey PRIMARY KEY (id);
 F   ALTER TABLE ONLY public.annotations DROP CONSTRAINT annotations_pkey;
       public         postgres    false    161    161    2402         �           2606    228665    constructs_morphisms_pkey 
   CONSTRAINT     |   ALTER TABLE ONLY constructs_morphisms
    ADD CONSTRAINT constructs_morphisms_pkey PRIMARY KEY (construct_id, morphism_id);
 X   ALTER TABLE ONLY public.constructs_morphisms DROP CONSTRAINT constructs_morphisms_pkey;
       public         postgres    false    164    164    164    2402         �           2606    228673    datasources_pkey 
   CONSTRAINT     S   ALTER TABLE ONLY datasources
    ADD CONSTRAINT datasources_pkey PRIMARY KEY (id);
 F   ALTER TABLE ONLY public.datasources DROP CONSTRAINT datasources_pkey;
       public         postgres    false    165    165    2402         �           2606    228683    dataspace_datasources_pkey 
   CONSTRAINT     �   ALTER TABLE ONLY dataspace_datasources
    ADD CONSTRAINT dataspace_datasources_pkey PRIMARY KEY (dataspace_id, datasource_id);
 Z   ALTER TABLE ONLY public.dataspace_datasources DROP CONSTRAINT dataspace_datasources_pkey;
       public         postgres    false    167    167    167    2402         �           2606    228688    dataspace_schemas_pkey 
   CONSTRAINT     t   ALTER TABLE ONLY dataspace_schemas
    ADD CONSTRAINT dataspace_schemas_pkey PRIMARY KEY (dataspace_id, schema_id);
 R   ALTER TABLE ONLY public.dataspace_schemas DROP CONSTRAINT dataspace_schemas_pkey;
       public         postgres    false    168    168    168    2402         �           2606    228693    dataspace_users_pkey 
   CONSTRAINT     n   ALTER TABLE ONLY dataspace_users
    ADD CONSTRAINT dataspace_users_pkey PRIMARY KEY (dataspace_id, user_id);
 N   ALTER TABLE ONLY public.dataspace_users DROP CONSTRAINT dataspace_users_pkey;
       public         postgres    false    169    169    169    2402         �           2606    228678    dataspaces_pkey 
   CONSTRAINT     Q   ALTER TABLE ONLY dataspaces
    ADD CONSTRAINT dataspaces_pkey PRIMARY KEY (id);
 D   ALTER TABLE ONLY public.dataspaces DROP CONSTRAINT dataspaces_pkey;
       public         postgres    false    166    166    2402         �           2606    228701    joinoperator_pkey 
   CONSTRAINT     U   ALTER TABLE ONLY joinoperator
    ADD CONSTRAINT joinoperator_pkey PRIMARY KEY (id);
 H   ALTER TABLE ONLY public.joinoperator DROP CONSTRAINT joinoperator_pkey;
       public         postgres    false    170    170    2402         �           2606    228709    kernel_density_estimators_pkey 
   CONSTRAINT     o   ALTER TABLE ONLY kernel_density_estimators
    ADD CONSTRAINT kernel_density_estimators_pkey PRIMARY KEY (id);
 b   ALTER TABLE ONLY public.kernel_density_estimators DROP CONSTRAINT kernel_density_estimators_pkey;
       public         postgres    false    171    171    2402         �           2606    228717    mappings_pkey 
   CONSTRAINT     M   ALTER TABLE ONLY mappings
    ADD CONSTRAINT mappings_pkey PRIMARY KEY (id);
 @   ALTER TABLE ONLY public.mappings DROP CONSTRAINT mappings_pkey;
       public         postgres    false    172    172    2402         �           2606    228722    morphism_constructs1_pkey 
   CONSTRAINT     }   ALTER TABLE ONLY morphism_constructs1
    ADD CONSTRAINT morphism_constructs1_pkey PRIMARY KEY (morphism_id, construct1_id);
 X   ALTER TABLE ONLY public.morphism_constructs1 DROP CONSTRAINT morphism_constructs1_pkey;
       public         postgres    false    173    173    173    2402         �           2606    228727    morphism_constructs2_pkey 
   CONSTRAINT     }   ALTER TABLE ONLY morphism_constructs2
    ADD CONSTRAINT morphism_constructs2_pkey PRIMARY KEY (morphism_id, construct2_id);
 X   ALTER TABLE ONLY public.morphism_constructs2 DROP CONSTRAINT morphism_constructs2_pkey;
       public         postgres    false    174    174    174    2402         �           2606    228735    one_to_one_matchings_pkey 
   CONSTRAINT     e   ALTER TABLE ONLY one_to_one_matchings
    ADD CONSTRAINT one_to_one_matchings_pkey PRIMARY KEY (id);
 X   ALTER TABLE ONLY public.one_to_one_matchings DROP CONSTRAINT one_to_one_matchings_pkey;
       public         postgres    false    175    175    2402         �           2606    228743    ontology_terms_pkey 
   CONSTRAINT     Y   ALTER TABLE ONLY ontology_terms
    ADD CONSTRAINT ontology_terms_pkey PRIMARY KEY (id);
 L   ALTER TABLE ONLY public.ontology_terms DROP CONSTRAINT ontology_terms_pkey;
       public         postgres    false    176    176    2402         �           2606    228759 #   parameter_applied_to_construct_pkey 
   CONSTRAINT     �   ALTER TABLE ONLY parameter_applied_to_construct
    ADD CONSTRAINT parameter_applied_to_construct_pkey PRIMARY KEY (parameter_id, construct_id);
 l   ALTER TABLE ONLY public.parameter_applied_to_construct DROP CONSTRAINT parameter_applied_to_construct_pkey;
       public         postgres    false    179    179    179    2402         �           2606    228754    parameters_pkey 
   CONSTRAINT     Q   ALTER TABLE ONLY parameters
    ADD CONSTRAINT parameters_pkey PRIMARY KEY (id);
 D   ALTER TABLE ONLY public.parameters DROP CONSTRAINT parameters_pkey;
       public         postgres    false    178    178    2402         �           2606    228764 /   participation_of_cmc_in_super_relationship_pkey 
   CONSTRAINT     �   ALTER TABLE ONLY participation_of_cmc_in_super_relationship
    ADD CONSTRAINT participation_of_cmc_in_super_relationship_pkey PRIMARY KEY (id);
 �   ALTER TABLE ONLY public.participation_of_cmc_in_super_relationship DROP CONSTRAINT participation_of_cmc_in_super_relationship_pkey;
       public         postgres    false    180    180    2402         �           2606    228769 +   participation_specifying_super_lexical_pkey 
   CONSTRAINT     �   ALTER TABLE ONLY participation_specifying_super_lexical
    ADD CONSTRAINT participation_specifying_super_lexical_pkey PRIMARY KEY (super_lexical_id, participation_in_super_relationship_id);
 |   ALTER TABLE ONLY public.participation_specifying_super_lexical DROP CONSTRAINT participation_specifying_super_lexical_pkey;
       public         postgres    false    181    181    181    2402         �           2606    228777    predicates_pkey 
   CONSTRAINT     Q   ALTER TABLE ONLY predicates
    ADD CONSTRAINT predicates_pkey PRIMARY KEY (id);
 D   ALTER TABLE ONLY public.predicates DROP CONSTRAINT predicates_pkey;
       public         postgres    false    182    182    2402         �           2606    228785    properties_pkey 
   CONSTRAINT     Q   ALTER TABLE ONLY properties
    ADD CONSTRAINT properties_pkey PRIMARY KEY (id);
 D   ALTER TABLE ONLY public.properties DROP CONSTRAINT properties_pkey;
       public         postgres    false    183    183    2402         �           2606    228793    queries_pkey 
   CONSTRAINT     K   ALTER TABLE ONLY queries
    ADD CONSTRAINT queries_pkey PRIMARY KEY (id);
 >   ALTER TABLE ONLY public.queries DROP CONSTRAINT queries_pkey;
       public         postgres    false    184    184    2402         �           2606    228798    query_datasources_pkey 
   CONSTRAINT     t   ALTER TABLE ONLY query_datasources
    ADD CONSTRAINT query_datasources_pkey PRIMARY KEY (query_id, datasource_id);
 R   ALTER TABLE ONLY public.query_datasources DROP CONSTRAINT query_datasources_pkey;
       public         postgres    false    185    185    185    2402                    2606    228808    query_result_mapping_pkey 
   CONSTRAINT     ~   ALTER TABLE ONLY query_result_mapping
    ADD CONSTRAINT query_result_mapping_pkey PRIMARY KEY (query_result_id, mapping_id);
 X   ALTER TABLE ONLY public.query_result_mapping DROP CONSTRAINT query_result_mapping_pkey;
       public         postgres    false    187    187    187    2402                    2606    228816 '   query_result_schema_of_data_source_pkey 
   CONSTRAINT     �   ALTER TABLE ONLY query_result_schema_of_data_source
    ADD CONSTRAINT query_result_schema_of_data_source_pkey PRIMARY KEY (query_result_id, schema_of_data_source_id);
 t   ALTER TABLE ONLY public.query_result_schema_of_data_source DROP CONSTRAINT query_result_schema_of_data_source_pkey;
       public         postgres    false    189    189    189    2402                    2606    228803    query_results_pkey 
   CONSTRAINT     W   ALTER TABLE ONLY query_results
    ADD CONSTRAINT query_results_pkey PRIMARY KEY (id);
 J   ALTER TABLE ONLY public.query_results DROP CONSTRAINT query_results_pkey;
       public         postgres    false    186    186    2402                    2606    228821    query_schemas_pkey 
   CONSTRAINT     h   ALTER TABLE ONLY query_schemas
    ADD CONSTRAINT query_schemas_pkey PRIMARY KEY (query_id, schema_id);
 J   ALTER TABLE ONLY public.query_schemas DROP CONSTRAINT query_schemas_pkey;
       public         postgres    false    190    190    190    2402         	           2606    228826    query_super_abstracts_pkey 
   CONSTRAINT     �   ALTER TABLE ONLY query_super_abstracts
    ADD CONSTRAINT query_super_abstracts_pkey PRIMARY KEY (query_id, super_abstract_id);
 Z   ALTER TABLE ONLY public.query_super_abstracts DROP CONSTRAINT query_super_abstracts_pkey;
       public         postgres    false    191    191    191    2402                    2606    228834    reconciling_expression_pkey 
   CONSTRAINT     i   ALTER TABLE ONLY reconciling_expression
    ADD CONSTRAINT reconciling_expression_pkey PRIMARY KEY (id);
 \   ALTER TABLE ONLY public.reconciling_expression DROP CONSTRAINT reconciling_expression_pkey;
       public         postgres    false    192    192    2402         !           2606    228895    reduceoperator_pkey 
   CONSTRAINT     Y   ALTER TABLE ONLY reduceoperator
    ADD CONSTRAINT reduceoperator_pkey PRIMARY KEY (id);
 L   ALTER TABLE ONLY public.reduceoperator DROP CONSTRAINT reduceoperator_pkey;
       public         postgres    false    202    202    2402         #           2606    228903    renameoperator_pkey 
   CONSTRAINT     Y   ALTER TABLE ONLY renameoperator
    ADD CONSTRAINT renameoperator_pkey PRIMARY KEY (id);
 L   ALTER TABLE ONLY public.renameoperator DROP CONSTRAINT renameoperator_pkey;
       public         postgres    false    203    203    2402                    2606    228850    result_field_result_values_pkey 
   CONSTRAINT     q   ALTER TABLE ONLY result_field_result_values
    ADD CONSTRAINT result_field_result_values_pkey PRIMARY KEY (id);
 d   ALTER TABLE ONLY public.result_field_result_values DROP CONSTRAINT result_field_result_values_pkey;
       public         postgres    false    194    194    2402                    2606    228842    result_fields_pkey 
   CONSTRAINT     W   ALTER TABLE ONLY result_fields
    ADD CONSTRAINT result_fields_pkey PRIMARY KEY (id);
 J   ALTER TABLE ONLY public.result_fields DROP CONSTRAINT result_fields_pkey;
       public         postgres    false    193    193    2402                    2606    228860    result_instance_mapping_pkey 
   CONSTRAINT     �   ALTER TABLE ONLY result_instance_mapping
    ADD CONSTRAINT result_instance_mapping_pkey PRIMARY KEY (result_instance_id, mapping_id);
 ^   ALTER TABLE ONLY public.result_instance_mapping DROP CONSTRAINT result_instance_mapping_pkey;
       public         postgres    false    196    196    196    2402                    2606    228865 "   result_instance_result_values_pkey 
   CONSTRAINT     �   ALTER TABLE ONLY result_instance_result_values
    ADD CONSTRAINT result_instance_result_values_pkey PRIMARY KEY (result_instance_id, result_field_result_values_id);
 j   ALTER TABLE ONLY public.result_instance_result_values DROP CONSTRAINT result_instance_result_values_pkey;
       public         postgres    false    197    197    197    2402                    2606    228855    result_instances_pkey 
   CONSTRAINT     ]   ALTER TABLE ONLY result_instances
    ADD CONSTRAINT result_instances_pkey PRIMARY KEY (id);
 P   ALTER TABLE ONLY public.result_instances DROP CONSTRAINT result_instances_pkey;
       public         postgres    false    195    195    2402                    2606    228875    result_type_result_fields_pkey 
   CONSTRAINT     �   ALTER TABLE ONLY result_type_result_fields
    ADD CONSTRAINT result_type_result_fields_pkey PRIMARY KEY (result_type_id, result_field_id);
 b   ALTER TABLE ONLY public.result_type_result_fields DROP CONSTRAINT result_type_result_fields_pkey;
       public         postgres    false    199    199    199    2402                    2606    228877 -   result_type_result_fields_result_field_id_key 
   CONSTRAINT     �   ALTER TABLE ONLY result_type_result_fields
    ADD CONSTRAINT result_type_result_fields_result_field_id_key UNIQUE (result_field_id);
 q   ALTER TABLE ONLY public.result_type_result_fields DROP CONSTRAINT result_type_result_fields_result_field_id_key;
       public         postgres    false    199    199    2402                    2606    228870    result_types_pkey 
   CONSTRAINT     U   ALTER TABLE ONLY result_types
    ADD CONSTRAINT result_types_pkey PRIMARY KEY (id);
 H   ALTER TABLE ONLY public.result_types DROP CONSTRAINT result_types_pkey;
       public         postgres    false    198    198    2402                    2606    228887    role_users_pkey 
   CONSTRAINT     _   ALTER TABLE ONLY role_users
    ADD CONSTRAINT role_users_pkey PRIMARY KEY (role_id, user_id);
 D   ALTER TABLE ONLY public.role_users DROP CONSTRAINT role_users_pkey;
       public         postgres    false    201    201    201    2402                    2606    228882 
   roles_pkey 
   CONSTRAINT     G   ALTER TABLE ONLY roles
    ADD CONSTRAINT roles_pkey PRIMARY KEY (id);
 :   ALTER TABLE ONLY public.roles DROP CONSTRAINT roles_pkey;
       public         postgres    false    200    200    2402         %           2606    228908    sample_kde_pkey 
   CONSTRAINT     Q   ALTER TABLE ONLY sample_kde
    ADD CONSTRAINT sample_kde_pkey PRIMARY KEY (id);
 D   ALTER TABLE ONLY public.sample_kde DROP CONSTRAINT sample_kde_pkey;
       public         postgres    false    204    204    2402         =           2606    228980    scanoperator_pkey 
   CONSTRAINT     U   ALTER TABLE ONLY scanoperator
    ADD CONSTRAINT scanoperator_pkey PRIMARY KEY (id);
 H   ALTER TABLE ONLY public.scanoperator DROP CONSTRAINT scanoperator_pkey;
       public         postgres    false    214    214    2402         '           2606    228916    schemas_pkey 
   CONSTRAINT     K   ALTER TABLE ONLY schemas
    ADD CONSTRAINT schemas_pkey PRIMARY KEY (id);
 >   ALTER TABLE ONLY public.schemas DROP CONSTRAINT schemas_pkey;
       public         postgres    false    205    205    2402         3           2606    228948 ?   schematic_correspondence_reconci_reconciling_expression_id_key1 
   CONSTRAINT     �   ALTER TABLE ONLY schematic_correspondence_reconciling_expressions_2
    ADD CONSTRAINT schematic_correspondence_reconci_reconciling_expression_id_key1 UNIQUE (reconciling_expression_id);
 �   ALTER TABLE ONLY public.schematic_correspondence_reconciling_expressions_2 DROP CONSTRAINT schematic_correspondence_reconci_reconciling_expression_id_key1;
       public         postgres    false    210    210    2402         /           2606    228941 ?   schematic_correspondence_reconcil_reconciling_expression_id_key 
   CONSTRAINT     �   ALTER TABLE ONLY schematic_correspondence_reconciling_expressions_1
    ADD CONSTRAINT schematic_correspondence_reconcil_reconciling_expression_id_key UNIQUE (reconciling_expression_id);
 �   ALTER TABLE ONLY public.schematic_correspondence_reconciling_expressions_1 DROP CONSTRAINT schematic_correspondence_reconcil_reconciling_expression_id_key;
       public         postgres    false    209    209    2402         1           2606    228939 7   schematic_correspondence_reconciling_expressions_1_pkey 
   CONSTRAINT     �   ALTER TABLE ONLY schematic_correspondence_reconciling_expressions_1
    ADD CONSTRAINT schematic_correspondence_reconciling_expressions_1_pkey PRIMARY KEY (schematic_correspondence_id, reconciling_expression_id);
 �   ALTER TABLE ONLY public.schematic_correspondence_reconciling_expressions_1 DROP CONSTRAINT schematic_correspondence_reconciling_expressions_1_pkey;
       public         postgres    false    209    209    209    2402         5           2606    228946 7   schematic_correspondence_reconciling_expressions_2_pkey 
   CONSTRAINT     �   ALTER TABLE ONLY schematic_correspondence_reconciling_expressions_2
    ADD CONSTRAINT schematic_correspondence_reconciling_expressions_2_pkey PRIMARY KEY (schematic_correspondence_id, reconciling_expression_id);
 �   ALTER TABLE ONLY public.schematic_correspondence_reconciling_expressions_2 DROP CONSTRAINT schematic_correspondence_reconciling_expressions_2_pkey;
       public         postgres    false    210    210    210    2402         )           2606    228924    schematic_correspondences_pkey 
   CONSTRAINT     o   ALTER TABLE ONLY schematic_correspondences
    ADD CONSTRAINT schematic_correspondences_pkey PRIMARY KEY (id);
 b   ALTER TABLE ONLY public.schematic_correspondences DROP CONSTRAINT schematic_correspondences_pkey;
       public         postgres    false    206    206    2402         +           2606    228929 4   schematic_correspondences_to_mapping_provenance_pkey 
   CONSTRAINT     �   ALTER TABLE ONLY schematic_correspondences_to_mapping_provenance
    ADD CONSTRAINT schematic_correspondences_to_mapping_provenance_pkey PRIMARY KEY (id);
 �   ALTER TABLE ONLY public.schematic_correspondences_to_mapping_provenance DROP CONSTRAINT schematic_correspondences_to_mapping_provenance_pkey;
       public         postgres    false    207    207    2402         -           2606    228934 ?   schematic_correspondences_utilised_for_mappings_provenance_pkey 
   CONSTRAINT     �   ALTER TABLE ONLY schematic_correspondences_utilised_for_mappings_provenance
    ADD CONSTRAINT schematic_correspondences_utilised_for_mappings_provenance_pkey PRIMARY KEY (provenance_id, schematic_correspondence_id);
 �   ALTER TABLE ONLY public.schematic_correspondences_utilised_for_mappings_provenance DROP CONSTRAINT schematic_correspondences_utilised_for_mappings_provenance_pkey;
       public         postgres    false    208    208    208    2402         ?           2606    228988    setoperator_pkey 
   CONSTRAINT     S   ALTER TABLE ONLY setoperator
    ADD CONSTRAINT setoperator_pkey PRIMARY KEY (id);
 F   ALTER TABLE ONLY public.setoperator DROP CONSTRAINT setoperator_pkey;
       public         postgres    false    215    215    2402         7           2606    228956    super_abstracts_pkey 
   CONSTRAINT     [   ALTER TABLE ONLY super_abstracts
    ADD CONSTRAINT super_abstracts_pkey PRIMARY KEY (id);
 N   ALTER TABLE ONLY public.super_abstracts DROP CONSTRAINT super_abstracts_pkey;
       public         postgres    false    211    211    2402         9           2606    228964    super_lexicals_pkey 
   CONSTRAINT     Y   ALTER TABLE ONLY super_lexicals
    ADD CONSTRAINT super_lexicals_pkey PRIMARY KEY (id);
 L   ALTER TABLE ONLY public.super_lexicals DROP CONSTRAINT super_lexicals_pkey;
       public         postgres    false    212    212    2402         ;           2606    228972    super_relationships_pkey 
   CONSTRAINT     c   ALTER TABLE ONLY super_relationships
    ADD CONSTRAINT super_relationships_pkey PRIMARY KEY (id);
 V   ALTER TABLE ONLY public.super_relationships DROP CONSTRAINT super_relationships_pkey;
       public         postgres    false    213    213    2402         A           2606    228996    temp_construct_pkey 
   CONSTRAINT     Y   ALTER TABLE ONLY temp_construct
    ADD CONSTRAINT temp_construct_pkey PRIMARY KEY (id);
 L   ALTER TABLE ONLY public.temp_construct DROP CONSTRAINT temp_construct_pkey;
       public         postgres    false    216    216    2402         C           2606    229004    typecastoperator_pkey 
   CONSTRAINT     ]   ALTER TABLE ONLY typecastoperator
    ADD CONSTRAINT typecastoperator_pkey PRIMARY KEY (id);
 P   ALTER TABLE ONLY public.typecastoperator DROP CONSTRAINT typecastoperator_pkey;
       public         postgres    false    217    217    2402         E           2606    229014    users_email_key 
   CONSTRAINT     J   ALTER TABLE ONLY users
    ADD CONSTRAINT users_email_key UNIQUE (email);
 ?   ALTER TABLE ONLY public.users DROP CONSTRAINT users_email_key;
       public         postgres    false    218    218    2402         G           2606    229012 
   users_pkey 
   CONSTRAINT     G   ALTER TABLE ONLY users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);
 :   ALTER TABLE ONLY public.users DROP CONSTRAINT users_pkey;
       public         postgres    false    218    218    2402         I           2606    229016    users_user_name_key 
   CONSTRAINT     R   ALTER TABLE ONLY users
    ADD CONSTRAINT users_user_name_key UNIQUE (user_name);
 C   ALTER TABLE ONLY public.users DROP CONSTRAINT users_user_name_key;
       public         postgres    false    218    218    2402         {           2606    229262    fk1c06aa3b5914c355    FK CONSTRAINT     t   ALTER TABLE ONLY query_schemas
    ADD CONSTRAINT fk1c06aa3b5914c355 FOREIGN KEY (query_id) REFERENCES queries(id);
 J   ALTER TABLE ONLY public.query_schemas DROP CONSTRAINT fk1c06aa3b5914c355;
       public       postgres    false    190    2044    184    2402         z           2606    229257    fk1c06aa3b880c25a4    FK CONSTRAINT     u   ALTER TABLE ONLY query_schemas
    ADD CONSTRAINT fk1c06aa3b880c25a4 FOREIGN KEY (schema_id) REFERENCES schemas(id);
 J   ALTER TABLE ONLY public.query_schemas DROP CONSTRAINT fk1c06aa3b880c25a4;
       public       postgres    false    190    2086    205    2402         Q           2606    229052    fk21d2c10f880c25a4    FK CONSTRAINT     y   ALTER TABLE ONLY dataspace_schemas
    ADD CONSTRAINT fk21d2c10f880c25a4 FOREIGN KEY (schema_id) REFERENCES schemas(id);
 N   ALTER TABLE ONLY public.dataspace_schemas DROP CONSTRAINT fk21d2c10f880c25a4;
       public       postgres    false    2086    205    168    2402         R           2606    229057    fk21d2c10fa5346195    FK CONSTRAINT        ALTER TABLE ONLY dataspace_schemas
    ADD CONSTRAINT fk21d2c10fa5346195 FOREIGN KEY (dataspace_id) REFERENCES dataspaces(id);
 N   ALTER TABLE ONLY public.dataspace_schemas DROP CONSTRAINT fk21d2c10fa5346195;
       public       postgres    false    168    2010    166    2402         �           2606    229337    fk31aec838398bd4f0    FK CONSTRAINT     �   ALTER TABLE ONLY result_type_result_fields
    ADD CONSTRAINT fk31aec838398bd4f0 FOREIGN KEY (result_type_id) REFERENCES result_types(id);
 V   ALTER TABLE ONLY public.result_type_result_fields DROP CONSTRAINT fk31aec838398bd4f0;
       public       postgres    false    2070    199    198    2402         �           2606    229332    fk31aec838e80d60c4    FK CONSTRAINT     �   ALTER TABLE ONLY result_type_result_fields
    ADD CONSTRAINT fk31aec838e80d60c4 FOREIGN KEY (result_field_id) REFERENCES result_fields(id);
 V   ALTER TABLE ONLY public.result_type_result_fields DROP CONSTRAINT fk31aec838e80d60c4;
       public       postgres    false    2060    193    199    2402         a           2606    229132    fk35575e8b58e9243e    FK CONSTRAINT     �   ALTER TABLE ONLY parameter_applied_to_construct
    ADD CONSTRAINT fk35575e8b58e9243e FOREIGN KEY (parameter_id) REFERENCES parameters(id);
 [   ALTER TABLE ONLY public.parameter_applied_to_construct DROP CONSTRAINT fk35575e8b58e9243e;
       public       postgres    false    178    179    2032    2402         x           2606    229247    fk3d200fdb8af976df    FK CONSTRAINT     �   ALTER TABLE ONLY query_result_schema_of_data_source
    ADD CONSTRAINT fk3d200fdb8af976df FOREIGN KEY (schema_of_data_source_id) REFERENCES schemas(id);
 _   ALTER TABLE ONLY public.query_result_schema_of_data_source DROP CONSTRAINT fk3d200fdb8af976df;
       public       postgres    false    2086    205    189    2402         y           2606    229252    fk3d200fdba8a2e650    FK CONSTRAINT     �   ALTER TABLE ONLY query_result_schema_of_data_source
    ADD CONSTRAINT fk3d200fdba8a2e650 FOREIGN KEY (query_result_id) REFERENCES query_results(id);
 _   ALTER TABLE ONLY public.query_result_schema_of_data_source DROP CONSTRAINT fk3d200fdba8a2e650;
       public       postgres    false    2048    189    186    2402         M           2606    229032    fk47c8342045ade094    FK CONSTRAINT     �   ALTER TABLE ONLY annotation_constraining_constructs
    ADD CONSTRAINT fk47c8342045ade094 FOREIGN KEY (annotation_id) REFERENCES annotations(id);
 _   ALTER TABLE ONLY public.annotation_constraining_constructs DROP CONSTRAINT fk47c8342045ade094;
       public       postgres    false    163    161    2000    2402         }           2606    229272    fk482186f65914c355    FK CONSTRAINT     |   ALTER TABLE ONLY query_super_abstracts
    ADD CONSTRAINT fk482186f65914c355 FOREIGN KEY (query_id) REFERENCES queries(id);
 R   ALTER TABLE ONLY public.query_super_abstracts DROP CONSTRAINT fk482186f65914c355;
       public       postgres    false    191    184    2044    2402         |           2606    229267    fk482186f6bc26ce98    FK CONSTRAINT     �   ALTER TABLE ONLY query_super_abstracts
    ADD CONSTRAINT fk482186f6bc26ce98 FOREIGN KEY (super_abstract_id) REFERENCES super_abstracts(id);
 R   ALTER TABLE ONLY public.query_super_abstracts DROP CONSTRAINT fk482186f6bc26ce98;
       public       postgres    false    191    2102    211    2402         �           2606    229327    fk52a767bc7f33a796    FK CONSTRAINT     �   ALTER TABLE ONLY result_instance_result_values
    ADD CONSTRAINT fk52a767bc7f33a796 FOREIGN KEY (result_field_result_values_id) REFERENCES result_field_result_values(id);
 Z   ALTER TABLE ONLY public.result_instance_result_values DROP CONSTRAINT fk52a767bc7f33a796;
       public       postgres    false    197    194    2062    2402         �           2606    229322    fk52a767bcdebf6810    FK CONSTRAINT     �   ALTER TABLE ONLY result_instance_result_values
    ADD CONSTRAINT fk52a767bcdebf6810 FOREIGN KEY (result_instance_id) REFERENCES result_instances(id);
 Z   ALTER TABLE ONLY public.result_instance_result_values DROP CONSTRAINT fk52a767bcdebf6810;
       public       postgres    false    197    195    2064    2402         �           2606    229427    fk5371733ec94546c5    FK CONSTRAINT     �   ALTER TABLE ONLY schematic_correspondences_to_mapping_provenance
    ADD CONSTRAINT fk5371733ec94546c5 FOREIGN KEY (mapping_id) REFERENCES mappings(id);
 l   ALTER TABLE ONLY public.schematic_correspondences_to_mapping_provenance DROP CONSTRAINT fk5371733ec94546c5;
       public       postgres    false    172    207    2022    2402         L           2606    229027    fk57a47cb345ade094    FK CONSTRAINT     �   ALTER TABLE ONLY annotation_annotated_constructs
    ADD CONSTRAINT fk57a47cb345ade094 FOREIGN KEY (annotation_id) REFERENCES annotations(id);
 \   ALTER TABLE ONLY public.annotation_annotated_constructs DROP CONSTRAINT fk57a47cb345ade094;
       public       postgres    false    2000    162    161    2402         g           2606    229162    fk6218799c19d4357a    FK CONSTRAINT        ALTER TABLE ONLY predicates
    ADD CONSTRAINT fk6218799c19d4357a FOREIGN KEY (scan_predicate_id) REFERENCES scanoperator(id);
 G   ALTER TABLE ONLY public.predicates DROP CONSTRAINT fk6218799c19d4357a;
       public       postgres    false    214    182    2108    2402         i           2606    229172    fk6218799cfd52399a    FK CONSTRAINT        ALTER TABLE ONLY predicates
    ADD CONSTRAINT fk6218799cfd52399a FOREIGN KEY (join_predicate_id) REFERENCES joinoperator(id);
 G   ALTER TABLE ONLY public.predicates DROP CONSTRAINT fk6218799cfd52399a;
       public       postgres    false    170    182    2018    2402         _           2606    229122    fk660937bd79ff3e2e    FK CONSTRAINT     �   ALTER TABLE ONLY ontology_term_enum_values
    ADD CONSTRAINT fk660937bd79ff3e2e FOREIGN KEY (enum_id) REFERENCES ontology_terms(id);
 V   ALTER TABLE ONLY public.ontology_term_enum_values DROP CONSTRAINT fk660937bd79ff3e2e;
       public       postgres    false    2030    177    176    2402         �           2606    229432    fk757b51091820cbd9    FK CONSTRAINT     �   ALTER TABLE ONLY schematic_correspondences_utilised_for_mappings_provenance
    ADD CONSTRAINT fk757b51091820cbd9 FOREIGN KEY (schematic_correspondence_id) REFERENCES schematic_correspondences(id);
 w   ALTER TABLE ONLY public.schematic_correspondences_utilised_for_mappings_provenance DROP CONSTRAINT fk757b51091820cbd9;
       public       postgres    false    2088    208    206    2402         �           2606    229437    fk757b51097eb1d99    FK CONSTRAINT     �   ALTER TABLE ONLY schematic_correspondences_utilised_for_mappings_provenance
    ADD CONSTRAINT fk757b51097eb1d99 FOREIGN KEY (provenance_id) REFERENCES schematic_correspondences_to_mapping_provenance(id);
 v   ALTER TABLE ONLY public.schematic_correspondences_utilised_for_mappings_provenance DROP CONSTRAINT fk757b51097eb1d99;
       public       postgres    false    208    207    2090    2402         P           2606    229047    fk90a07c6ba5346195    FK CONSTRAINT     �   ALTER TABLE ONLY dataspace_datasources
    ADD CONSTRAINT fk90a07c6ba5346195 FOREIGN KEY (dataspace_id) REFERENCES dataspaces(id);
 R   ALTER TABLE ONLY public.dataspace_datasources DROP CONSTRAINT fk90a07c6ba5346195;
       public       postgres    false    167    2010    166    2402         O           2606    229042    fk90a07c6bfee88a44    FK CONSTRAINT     �   ALTER TABLE ONLY dataspace_datasources
    ADD CONSTRAINT fk90a07c6bfee88a44 FOREIGN KEY (datasource_id) REFERENCES datasources(id);
 R   ALTER TABLE ONLY public.dataspace_datasources DROP CONSTRAINT fk90a07c6bfee88a44;
       public       postgres    false    167    2008    165    2402         J           2606    229017    fk_annotation_ontology_term_id    FK CONSTRAINT     �   ALTER TABLE ONLY annotations
    ADD CONSTRAINT fk_annotation_ontology_term_id FOREIGN KEY (annotation_ontology_term_id) REFERENCES ontology_terms(id);
 T   ALTER TABLE ONLY public.annotations DROP CONSTRAINT fk_annotation_ontology_term_id;
       public       postgres    false    176    161    2030    2402         K           2606    229022    fk_annotation_user_id    FK CONSTRAINT     }   ALTER TABLE ONLY annotations
    ADD CONSTRAINT fk_annotation_user_id FOREIGN KEY (annotation_user_id) REFERENCES users(id);
 K   ALTER TABLE ONLY public.annotations DROP CONSTRAINT fk_annotation_user_id;
       public       postgres    false    2118    218    161    2402         �           2606    229462 .   fk_canonical_model_construct_schema_id3d29608d    FK CONSTRAINT     �   ALTER TABLE ONLY super_abstracts
    ADD CONSTRAINT fk_canonical_model_construct_schema_id3d29608d FOREIGN KEY (canonical_model_construct_schema_id) REFERENCES schemas(id);
 h   ALTER TABLE ONLY public.super_abstracts DROP CONSTRAINT fk_canonical_model_construct_schema_id3d29608d;
       public       postgres    false    211    2086    205    2402         �           2606    229507 .   fk_canonical_model_construct_schema_id63923737    FK CONSTRAINT     �   ALTER TABLE ONLY super_relationships
    ADD CONSTRAINT fk_canonical_model_construct_schema_id63923737 FOREIGN KEY (canonical_model_construct_schema_id) REFERENCES schemas(id);
 l   ALTER TABLE ONLY public.super_relationships DROP CONSTRAINT fk_canonical_model_construct_schema_id63923737;
       public       postgres    false    2086    205    213    2402         �           2606    229577 .   fk_canonical_model_construct_schema_id7232f10c    FK CONSTRAINT     �   ALTER TABLE ONLY temp_construct
    ADD CONSTRAINT fk_canonical_model_construct_schema_id7232f10c FOREIGN KEY (canonical_model_construct_schema_id) REFERENCES schemas(id);
 g   ALTER TABLE ONLY public.temp_construct DROP CONSTRAINT fk_canonical_model_construct_schema_id7232f10c;
       public       postgres    false    2086    205    216    2402         �           2606    229477 .   fk_canonical_model_construct_schema_idedb2b7f3    FK CONSTRAINT     �   ALTER TABLE ONLY super_lexicals
    ADD CONSTRAINT fk_canonical_model_construct_schema_idedb2b7f3 FOREIGN KEY (canonical_model_construct_schema_id) REFERENCES schemas(id);
 g   ALTER TABLE ONLY public.super_lexicals DROP CONSTRAINT fk_canonical_model_construct_schema_idedb2b7f3;
       public       postgres    false    205    212    2086    2402         �           2606    229542 *   fk_mapping_operator_result_type_id3e49da61    FK CONSTRAINT     �   ALTER TABLE ONLY scanoperator
    ADD CONSTRAINT fk_mapping_operator_result_type_id3e49da61 FOREIGN KEY (mapping_operator_result_type_id) REFERENCES result_types(id);
 a   ALTER TABLE ONLY public.scanoperator DROP CONSTRAINT fk_mapping_operator_result_type_id3e49da61;
       public       postgres    false    198    214    2070    2402         �           2606    229367 *   fk_mapping_operator_result_type_id5e11884a    FK CONSTRAINT     �   ALTER TABLE ONLY reduceoperator
    ADD CONSTRAINT fk_mapping_operator_result_type_id5e11884a FOREIGN KEY (mapping_operator_result_type_id) REFERENCES result_types(id);
 c   ALTER TABLE ONLY public.reduceoperator DROP CONSTRAINT fk_mapping_operator_result_type_id5e11884a;
       public       postgres    false    202    198    2070    2402         �           2606    229392 *   fk_mapping_operator_result_type_id94027222    FK CONSTRAINT     �   ALTER TABLE ONLY renameoperator
    ADD CONSTRAINT fk_mapping_operator_result_type_id94027222 FOREIGN KEY (mapping_operator_result_type_id) REFERENCES result_types(id);
 c   ALTER TABLE ONLY public.renameoperator DROP CONSTRAINT fk_mapping_operator_result_type_id94027222;
       public       postgres    false    203    198    2070    2402         X           2606    229087 *   fk_mapping_operator_result_type_id960ef40e    FK CONSTRAINT     �   ALTER TABLE ONLY joinoperator
    ADD CONSTRAINT fk_mapping_operator_result_type_id960ef40e FOREIGN KEY (mapping_operator_result_type_id) REFERENCES result_types(id);
 a   ALTER TABLE ONLY public.joinoperator DROP CONSTRAINT fk_mapping_operator_result_type_id960ef40e;
       public       postgres    false    2070    198    170    2402         �           2606    229602 *   fk_mapping_operator_result_type_ida776797d    FK CONSTRAINT     �   ALTER TABLE ONLY typecastoperator
    ADD CONSTRAINT fk_mapping_operator_result_type_ida776797d FOREIGN KEY (mapping_operator_result_type_id) REFERENCES result_types(id);
 e   ALTER TABLE ONLY public.typecastoperator DROP CONSTRAINT fk_mapping_operator_result_type_ida776797d;
       public       postgres    false    2070    198    217    2402         �           2606    229567 *   fk_mapping_operator_result_type_idbc266f06    FK CONSTRAINT     �   ALTER TABLE ONLY setoperator
    ADD CONSTRAINT fk_mapping_operator_result_type_idbc266f06 FOREIGN KEY (mapping_operator_result_type_id) REFERENCES result_types(id);
 `   ALTER TABLE ONLY public.setoperator DROP CONSTRAINT fk_mapping_operator_result_type_idbc266f06;
       public       postgres    false    215    2070    198    2402         [           2606    229102    fk_mapping_query1_id    FK CONSTRAINT     z   ALTER TABLE ONLY mappings
    ADD CONSTRAINT fk_mapping_query1_id FOREIGN KEY (mapping_query1_id) REFERENCES queries(id);
 G   ALTER TABLE ONLY public.mappings DROP CONSTRAINT fk_mapping_query1_id;
       public       postgres    false    184    2044    172    2402         \           2606    229107    fk_mapping_query2_id    FK CONSTRAINT     z   ALTER TABLE ONLY mappings
    ADD CONSTRAINT fk_mapping_query2_id FOREIGN KEY (mapping_query2_id) REFERENCES queries(id);
 G   ALTER TABLE ONLY public.mappings DROP CONSTRAINT fk_mapping_query2_id;
       public       postgres    false    2044    184    172    2402         b           2606    229137    fk_mmc_dataspace_id26f08d46    FK CONSTRAINT     �   ALTER TABLE ONLY participation_of_cmc_in_super_relationship
    ADD CONSTRAINT fk_mmc_dataspace_id26f08d46 FOREIGN KEY (mmc_dataspace_id) REFERENCES dataspaces(id);
 p   ALTER TABLE ONLY public.participation_of_cmc_in_super_relationship DROP CONSTRAINT fk_mmc_dataspace_id26f08d46;
       public       postgres    false    2010    166    180    2402         N           2606    229037    fk_mmc_dataspace_id4c4e2cae    FK CONSTRAINT     �   ALTER TABLE ONLY datasources
    ADD CONSTRAINT fk_mmc_dataspace_id4c4e2cae FOREIGN KEY (mmc_dataspace_id) REFERENCES dataspaces(id);
 Q   ALTER TABLE ONLY public.datasources DROP CONSTRAINT fk_mmc_dataspace_id4c4e2cae;
       public       postgres    false    165    2010    166    2402         l           2606    229187    fk_mmc_dataspace_id51d76346    FK CONSTRAINT     �   ALTER TABLE ONLY queries
    ADD CONSTRAINT fk_mmc_dataspace_id51d76346 FOREIGN KEY (mmc_dataspace_id) REFERENCES dataspaces(id);
 M   ALTER TABLE ONLY public.queries DROP CONSTRAINT fk_mmc_dataspace_id51d76346;
       public       postgres    false    166    2010    184    2402         �           2606    229472 #   fk_mmc_dataspace_id558d94a23d29608d    FK CONSTRAINT     �   ALTER TABLE ONLY super_abstracts
    ADD CONSTRAINT fk_mmc_dataspace_id558d94a23d29608d FOREIGN KEY (mmc_dataspace_id) REFERENCES dataspaces(id);
 ]   ALTER TABLE ONLY public.super_abstracts DROP CONSTRAINT fk_mmc_dataspace_id558d94a23d29608d;
       public       postgres    false    2010    166    211    2402         �           2606    229512 #   fk_mmc_dataspace_id558d94a263923737    FK CONSTRAINT     �   ALTER TABLE ONLY super_relationships
    ADD CONSTRAINT fk_mmc_dataspace_id558d94a263923737 FOREIGN KEY (mmc_dataspace_id) REFERENCES dataspaces(id);
 a   ALTER TABLE ONLY public.super_relationships DROP CONSTRAINT fk_mmc_dataspace_id558d94a263923737;
       public       postgres    false    213    2010    166    2402         �           2606    229582 #   fk_mmc_dataspace_id558d94a27232f10c    FK CONSTRAINT     �   ALTER TABLE ONLY temp_construct
    ADD CONSTRAINT fk_mmc_dataspace_id558d94a27232f10c FOREIGN KEY (mmc_dataspace_id) REFERENCES dataspaces(id);
 \   ALTER TABLE ONLY public.temp_construct DROP CONSTRAINT fk_mmc_dataspace_id558d94a27232f10c;
       public       postgres    false    166    2010    216    2402         �           2606    229482 #   fk_mmc_dataspace_id558d94a2edb2b7f3    FK CONSTRAINT     �   ALTER TABLE ONLY super_lexicals
    ADD CONSTRAINT fk_mmc_dataspace_id558d94a2edb2b7f3 FOREIGN KEY (mmc_dataspace_id) REFERENCES dataspaces(id);
 \   ALTER TABLE ONLY public.super_lexicals DROP CONSTRAINT fk_mmc_dataspace_id558d94a2edb2b7f3;
       public       postgres    false    2010    166    212    2402         �           2606    229297    fk_mmc_dataspace_id582304bc    FK CONSTRAINT     �   ALTER TABLE ONLY result_instances
    ADD CONSTRAINT fk_mmc_dataspace_id582304bc FOREIGN KEY (mmc_dataspace_id) REFERENCES dataspaces(id);
 V   ALTER TABLE ONLY public.result_instances DROP CONSTRAINT fk_mmc_dataspace_id582304bc;
       public       postgres    false    195    2010    166    2402         h           2606    229167    fk_mmc_dataspace_id6218799c    FK CONSTRAINT     �   ALTER TABLE ONLY predicates
    ADD CONSTRAINT fk_mmc_dataspace_id6218799c FOREIGN KEY (mmc_dataspace_id) REFERENCES dataspaces(id);
 P   ALTER TABLE ONLY public.predicates DROP CONSTRAINT fk_mmc_dataspace_id6218799c;
       public       postgres    false    182    166    2010    2402         �           2606    229292    fk_mmc_dataspace_id72e875bd    FK CONSTRAINT     �   ALTER TABLE ONLY result_field_result_values
    ADD CONSTRAINT fk_mmc_dataspace_id72e875bd FOREIGN KEY (mmc_dataspace_id) REFERENCES dataspaces(id);
 `   ALTER TABLE ONLY public.result_field_result_values DROP CONSTRAINT fk_mmc_dataspace_id72e875bd;
       public       postgres    false    2010    194    166    2402         �           2606    229407    fk_mmc_dataspace_id9d110ad2    FK CONSTRAINT     �   ALTER TABLE ONLY schemas
    ADD CONSTRAINT fk_mmc_dataspace_id9d110ad2 FOREIGN KEY (mmc_dataspace_id) REFERENCES dataspaces(id);
 M   ALTER TABLE ONLY public.schemas DROP CONSTRAINT fk_mmc_dataspace_id9d110ad2;
       public       postgres    false    205    166    2010    2402         �           2606    229527 #   fk_mmc_dataspace_ide9fcaf723e49da61    FK CONSTRAINT     �   ALTER TABLE ONLY scanoperator
    ADD CONSTRAINT fk_mmc_dataspace_ide9fcaf723e49da61 FOREIGN KEY (mmc_dataspace_id) REFERENCES dataspaces(id);
 Z   ALTER TABLE ONLY public.scanoperator DROP CONSTRAINT fk_mmc_dataspace_ide9fcaf723e49da61;
       public       postgres    false    2010    214    166    2402         �           2606    229357 #   fk_mmc_dataspace_ide9fcaf725e11884a    FK CONSTRAINT     �   ALTER TABLE ONLY reduceoperator
    ADD CONSTRAINT fk_mmc_dataspace_ide9fcaf725e11884a FOREIGN KEY (mmc_dataspace_id) REFERENCES dataspaces(id);
 \   ALTER TABLE ONLY public.reduceoperator DROP CONSTRAINT fk_mmc_dataspace_ide9fcaf725e11884a;
       public       postgres    false    166    202    2010    2402         �           2606    229382 #   fk_mmc_dataspace_ide9fcaf7294027222    FK CONSTRAINT     �   ALTER TABLE ONLY renameoperator
    ADD CONSTRAINT fk_mmc_dataspace_ide9fcaf7294027222 FOREIGN KEY (mmc_dataspace_id) REFERENCES dataspaces(id);
 \   ALTER TABLE ONLY public.renameoperator DROP CONSTRAINT fk_mmc_dataspace_ide9fcaf7294027222;
       public       postgres    false    2010    166    203    2402         V           2606    229077 #   fk_mmc_dataspace_ide9fcaf72960ef40e    FK CONSTRAINT     �   ALTER TABLE ONLY joinoperator
    ADD CONSTRAINT fk_mmc_dataspace_ide9fcaf72960ef40e FOREIGN KEY (mmc_dataspace_id) REFERENCES dataspaces(id);
 Z   ALTER TABLE ONLY public.joinoperator DROP CONSTRAINT fk_mmc_dataspace_ide9fcaf72960ef40e;
       public       postgres    false    2010    166    170    2402         �           2606    229592 #   fk_mmc_dataspace_ide9fcaf72a776797d    FK CONSTRAINT     �   ALTER TABLE ONLY typecastoperator
    ADD CONSTRAINT fk_mmc_dataspace_ide9fcaf72a776797d FOREIGN KEY (mmc_dataspace_id) REFERENCES dataspaces(id);
 ^   ALTER TABLE ONLY public.typecastoperator DROP CONSTRAINT fk_mmc_dataspace_ide9fcaf72a776797d;
       public       postgres    false    217    166    2010    2402         �           2606    229557 #   fk_mmc_dataspace_ide9fcaf72bc266f06    FK CONSTRAINT     �   ALTER TABLE ONLY setoperator
    ADD CONSTRAINT fk_mmc_dataspace_ide9fcaf72bc266f06 FOREIGN KEY (mmc_dataspace_id) REFERENCES dataspaces(id);
 Y   ALTER TABLE ONLY public.setoperator DROP CONSTRAINT fk_mmc_dataspace_ide9fcaf72bc266f06;
       public       postgres    false    166    215    2010    2402         q           2606    229212    fk_mmc_dataspace_ideb2c673f    FK CONSTRAINT     �   ALTER TABLE ONLY query_results
    ADD CONSTRAINT fk_mmc_dataspace_ideb2c673f FOREIGN KEY (mmc_dataspace_id) REFERENCES dataspaces(id);
 S   ALTER TABLE ONLY public.query_results DROP CONSTRAINT fk_mmc_dataspace_ideb2c673f;
       public       postgres    false    2010    186    166    2402         ]           2606    229112 +   fk_mmc_dataspace_idf771ce9b158d101d25461f52    FK CONSTRAINT     �   ALTER TABLE ONLY one_to_one_matchings
    ADD CONSTRAINT fk_mmc_dataspace_idf771ce9b158d101d25461f52 FOREIGN KEY (mmc_dataspace_id) REFERENCES dataspaces(id);
 j   ALTER TABLE ONLY public.one_to_one_matchings DROP CONSTRAINT fk_mmc_dataspace_idf771ce9b158d101d25461f52;
       public       postgres    false    175    166    2010    2402         Z           2606    229097 #   fk_mmc_dataspace_idf771ce9b3a3cf165    FK CONSTRAINT     �   ALTER TABLE ONLY mappings
    ADD CONSTRAINT fk_mmc_dataspace_idf771ce9b3a3cf165 FOREIGN KEY (mmc_dataspace_id) REFERENCES dataspaces(id);
 V   ALTER TABLE ONLY public.mappings DROP CONSTRAINT fk_mmc_dataspace_idf771ce9b3a3cf165;
       public       postgres    false    2010    172    166    2402         �           2606    229422 #   fk_mmc_dataspace_idf771ce9b4971e6e7    FK CONSTRAINT     �   ALTER TABLE ONLY schematic_correspondences
    ADD CONSTRAINT fk_mmc_dataspace_idf771ce9b4971e6e7 FOREIGN KEY (mmc_dataspace_id) REFERENCES dataspaces(id);
 g   ALTER TABLE ONLY public.schematic_correspondences DROP CONSTRAINT fk_mmc_dataspace_idf771ce9b4971e6e7;
       public       postgres    false    206    166    2010    2402         ^           2606    229117 (   fk_ontology_term_parent_ontology_term_id    FK CONSTRAINT     �   ALTER TABLE ONLY ontology_terms
    ADD CONSTRAINT fk_ontology_term_parent_ontology_term_id FOREIGN KEY (parent_ontology_term_id) REFERENCES ontology_terms(id);
 a   ALTER TABLE ONLY public.ontology_terms DROP CONSTRAINT fk_ontology_term_parent_ontology_term_id;
       public       postgres    false    176    2030    176    2402         �           2606    229537 "   fk_operator_data_source_id3e49da61    FK CONSTRAINT     �   ALTER TABLE ONLY scanoperator
    ADD CONSTRAINT fk_operator_data_source_id3e49da61 FOREIGN KEY (mapping_operator_data_source_id) REFERENCES datasources(id);
 Y   ALTER TABLE ONLY public.scanoperator DROP CONSTRAINT fk_operator_data_source_id3e49da61;
       public       postgres    false    214    165    2008    2402         �           2606    229362 "   fk_operator_data_source_id5e11884a    FK CONSTRAINT     �   ALTER TABLE ONLY reduceoperator
    ADD CONSTRAINT fk_operator_data_source_id5e11884a FOREIGN KEY (mapping_operator_data_source_id) REFERENCES datasources(id);
 [   ALTER TABLE ONLY public.reduceoperator DROP CONSTRAINT fk_operator_data_source_id5e11884a;
       public       postgres    false    202    165    2008    2402         �           2606    229387 "   fk_operator_data_source_id94027222    FK CONSTRAINT     �   ALTER TABLE ONLY renameoperator
    ADD CONSTRAINT fk_operator_data_source_id94027222 FOREIGN KEY (mapping_operator_data_source_id) REFERENCES datasources(id);
 [   ALTER TABLE ONLY public.renameoperator DROP CONSTRAINT fk_operator_data_source_id94027222;
       public       postgres    false    165    203    2008    2402         W           2606    229082 "   fk_operator_data_source_id960ef40e    FK CONSTRAINT     �   ALTER TABLE ONLY joinoperator
    ADD CONSTRAINT fk_operator_data_source_id960ef40e FOREIGN KEY (mapping_operator_data_source_id) REFERENCES datasources(id);
 Y   ALTER TABLE ONLY public.joinoperator DROP CONSTRAINT fk_operator_data_source_id960ef40e;
       public       postgres    false    170    165    2008    2402         �           2606    229597 "   fk_operator_data_source_ida776797d    FK CONSTRAINT     �   ALTER TABLE ONLY typecastoperator
    ADD CONSTRAINT fk_operator_data_source_ida776797d FOREIGN KEY (mapping_operator_data_source_id) REFERENCES datasources(id);
 ]   ALTER TABLE ONLY public.typecastoperator DROP CONSTRAINT fk_operator_data_source_ida776797d;
       public       postgres    false    217    165    2008    2402         �           2606    229562 "   fk_operator_data_source_idbc266f06    FK CONSTRAINT     �   ALTER TABLE ONLY setoperator
    ADD CONSTRAINT fk_operator_data_source_idbc266f06 FOREIGN KEY (mapping_operator_data_source_id) REFERENCES datasources(id);
 X   ALTER TABLE ONLY public.setoperator DROP CONSTRAINT fk_operator_data_source_idbc266f06;
       public       postgres    false    165    2008    215    2402         �           2606    229522    fk_operator_mapping_id3e49da61    FK CONSTRAINT     �   ALTER TABLE ONLY scanoperator
    ADD CONSTRAINT fk_operator_mapping_id3e49da61 FOREIGN KEY (mapping_operator_mapping_id) REFERENCES mappings(id);
 U   ALTER TABLE ONLY public.scanoperator DROP CONSTRAINT fk_operator_mapping_id3e49da61;
       public       postgres    false    214    2022    172    2402         �           2606    229352    fk_operator_mapping_id5e11884a    FK CONSTRAINT     �   ALTER TABLE ONLY reduceoperator
    ADD CONSTRAINT fk_operator_mapping_id5e11884a FOREIGN KEY (mapping_operator_mapping_id) REFERENCES mappings(id);
 W   ALTER TABLE ONLY public.reduceoperator DROP CONSTRAINT fk_operator_mapping_id5e11884a;
       public       postgres    false    202    172    2022    2402         �           2606    229377    fk_operator_mapping_id94027222    FK CONSTRAINT     �   ALTER TABLE ONLY renameoperator
    ADD CONSTRAINT fk_operator_mapping_id94027222 FOREIGN KEY (mapping_operator_mapping_id) REFERENCES mappings(id);
 W   ALTER TABLE ONLY public.renameoperator DROP CONSTRAINT fk_operator_mapping_id94027222;
       public       postgres    false    203    172    2022    2402         U           2606    229072    fk_operator_mapping_id960ef40e    FK CONSTRAINT     �   ALTER TABLE ONLY joinoperator
    ADD CONSTRAINT fk_operator_mapping_id960ef40e FOREIGN KEY (mapping_operator_mapping_id) REFERENCES mappings(id);
 U   ALTER TABLE ONLY public.joinoperator DROP CONSTRAINT fk_operator_mapping_id960ef40e;
       public       postgres    false    2022    172    170    2402         �           2606    229587    fk_operator_mapping_ida776797d    FK CONSTRAINT     �   ALTER TABLE ONLY typecastoperator
    ADD CONSTRAINT fk_operator_mapping_ida776797d FOREIGN KEY (mapping_operator_mapping_id) REFERENCES mappings(id);
 Y   ALTER TABLE ONLY public.typecastoperator DROP CONSTRAINT fk_operator_mapping_ida776797d;
       public       postgres    false    2022    217    172    2402         �           2606    229552    fk_operator_mapping_idbc266f06    FK CONSTRAINT     �   ALTER TABLE ONLY setoperator
    ADD CONSTRAINT fk_operator_mapping_idbc266f06 FOREIGN KEY (mapping_operator_mapping_id) REFERENCES mappings(id);
 T   ALTER TABLE ONLY public.setoperator DROP CONSTRAINT fk_operator_mapping_idbc266f06;
       public       postgres    false    215    172    2022    2402         `           2606    229127 (   fk_parameter_schematic_correspondence_id    FK CONSTRAINT     �   ALTER TABLE ONLY parameters
    ADD CONSTRAINT fk_parameter_schematic_correspondence_id FOREIGN KEY (schematic_correspondence_id) REFERENCES schematic_correspondences(id);
 ]   ALTER TABLE ONLY public.parameters DROP CONSTRAINT fk_parameter_schematic_correspondence_id;
       public       postgres    false    2088    178    206    2402         c           2606    229142 &   fk_participation_super_relationship_id    FK CONSTRAINT     �   ALTER TABLE ONLY participation_of_cmc_in_super_relationship
    ADD CONSTRAINT fk_participation_super_relationship_id FOREIGN KEY (super_relationship_id) REFERENCES super_relationships(id);
 {   ALTER TABLE ONLY public.participation_of_cmc_in_super_relationship DROP CONSTRAINT fk_participation_super_relationship_id;
       public       postgres    false    2106    180    213    2402         f           2606    229157    fk_predicate_super_lexical1_id    FK CONSTRAINT     �   ALTER TABLE ONLY predicates
    ADD CONSTRAINT fk_predicate_super_lexical1_id FOREIGN KEY (predicate_super_lexical1_id) REFERENCES super_lexicals(id);
 S   ALTER TABLE ONLY public.predicates DROP CONSTRAINT fk_predicate_super_lexical1_id;
       public       postgres    false    212    182    2104    2402         j           2606    229177    fk_predicate_super_lexical2_id    FK CONSTRAINT     �   ALTER TABLE ONLY predicates
    ADD CONSTRAINT fk_predicate_super_lexical2_id FOREIGN KEY (predicate_super_lexical2_id) REFERENCES super_lexicals(id);
 S   ALTER TABLE ONLY public.predicates DROP CONSTRAINT fk_predicate_super_lexical2_id;
       public       postgres    false    2104    212    182    2402         m           2606    229192    fk_query_dataspace_id    FK CONSTRAINT     ~   ALTER TABLE ONLY queries
    ADD CONSTRAINT fk_query_dataspace_id FOREIGN KEY (query_dataspace_id) REFERENCES dataspaces(id);
 G   ALTER TABLE ONLY public.queries DROP CONSTRAINT fk_query_dataspace_id;
       public       postgres    false    184    166    2010    2402         s           2606    229222    fk_query_result_dataspace_id    FK CONSTRAINT     �   ALTER TABLE ONLY query_results
    ADD CONSTRAINT fk_query_result_dataspace_id FOREIGN KEY (query_result_dataspace_id) REFERENCES dataspaces(id);
 T   ALTER TABLE ONLY public.query_results DROP CONSTRAINT fk_query_result_dataspace_id;
       public       postgres    false    166    186    2010    2402         r           2606    229217    fk_query_result_query_id    FK CONSTRAINT     �   ALTER TABLE ONLY query_results
    ADD CONSTRAINT fk_query_result_query_id FOREIGN KEY (query_result_query_id) REFERENCES queries(id);
 P   ALTER TABLE ONLY public.query_results DROP CONSTRAINT fk_query_result_query_id;
       public       postgres    false    186    2044    184    2402         p           2606    229207    fk_query_result_result_type_id    FK CONSTRAINT     �   ALTER TABLE ONLY query_results
    ADD CONSTRAINT fk_query_result_result_type_id FOREIGN KEY (query_result_result_type_id) REFERENCES result_types(id);
 V   ALTER TABLE ONLY public.query_results DROP CONSTRAINT fk_query_result_result_type_id;
       public       postgres    false    186    198    2070    2402         k           2606    229182    fk_query_user_id    FK CONSTRAINT     o   ALTER TABLE ONLY queries
    ADD CONSTRAINT fk_query_user_id FOREIGN KEY (query_user_id) REFERENCES users(id);
 B   ALTER TABLE ONLY public.queries DROP CONSTRAINT fk_query_user_id;
       public       postgres    false    184    2118    218    2402         ~           2606    229277 '   fk_reconciling_expression_join_pred1_id    FK CONSTRAINT     �   ALTER TABLE ONLY reconciling_expression
    ADD CONSTRAINT fk_reconciling_expression_join_pred1_id FOREIGN KEY (reconciling_expression_join_pred_1) REFERENCES super_abstracts(id);
 h   ALTER TABLE ONLY public.reconciling_expression DROP CONSTRAINT fk_reconciling_expression_join_pred1_id;
       public       postgres    false    192    211    2102    2402         �           2606    229287 '   fk_reconciling_expression_join_pred2_id    FK CONSTRAINT     �   ALTER TABLE ONLY reconciling_expression
    ADD CONSTRAINT fk_reconciling_expression_join_pred2_id FOREIGN KEY (reconciling_expression_join_pred_2) REFERENCES super_abstracts(id);
 h   ALTER TABLE ONLY public.reconciling_expression DROP CONSTRAINT fk_reconciling_expression_join_pred2_id;
       public       postgres    false    192    2102    211    2402                    2606    229282 2   fk_reconciling_expression_target_super_abstract_id    FK CONSTRAINT     �   ALTER TABLE ONLY reconciling_expression
    ADD CONSTRAINT fk_reconciling_expression_target_super_abstract_id FOREIGN KEY (reconciling_expression_selection_target_super_abstract) REFERENCES super_abstracts(id);
 s   ALTER TABLE ONLY public.reconciling_expression DROP CONSTRAINT fk_reconciling_expression_target_super_abstract_id;
       public       postgres    false    192    2102    211    2402         �           2606    229307    fk_result_instance_query_id    FK CONSTRAINT     �   ALTER TABLE ONLY result_instances
    ADD CONSTRAINT fk_result_instance_query_id FOREIGN KEY (result_instance_query_id) REFERENCES queries(id);
 V   ALTER TABLE ONLY public.result_instances DROP CONSTRAINT fk_result_instance_query_id;
       public       postgres    false    184    2044    195    2402         �           2606    229302 !   fk_result_instance_result_type_id    FK CONSTRAINT     �   ALTER TABLE ONLY result_instances
    ADD CONSTRAINT fk_result_instance_result_type_id FOREIGN KEY (result_type_id) REFERENCES result_types(id);
 \   ALTER TABLE ONLY public.result_instances DROP CONSTRAINT fk_result_instance_result_type_id;
       public       postgres    false    195    198    2070    2402         �           2606    229402    fk_sample_estimator_id    FK CONSTRAINT     �   ALTER TABLE ONLY sample_kde
    ADD CONSTRAINT fk_sample_estimator_id FOREIGN KEY (estimator_id) REFERENCES kernel_density_estimators(id);
 K   ALTER TABLE ONLY public.sample_kde DROP CONSTRAINT fk_sample_estimator_id;
       public       postgres    false    204    171    2020    2402         �           2606    229532    fk_scan_super_abstract_id    FK CONSTRAINT     �   ALTER TABLE ONLY scanoperator
    ADD CONSTRAINT fk_scan_super_abstract_id FOREIGN KEY (scan_super_abstract_id) REFERENCES super_abstracts(id);
 P   ALTER TABLE ONLY public.scanoperator DROP CONSTRAINT fk_scan_super_abstract_id;
       public       postgres    false    211    2102    214    2402         �           2606    229412    fk_schema_datasource_id    FK CONSTRAINT     }   ALTER TABLE ONLY schemas
    ADD CONSTRAINT fk_schema_datasource_id FOREIGN KEY (data_source_id) REFERENCES datasources(id);
 I   ALTER TABLE ONLY public.schemas DROP CONSTRAINT fk_schema_datasource_id;
       public       postgres    false    205    165    2008    2402         �           2606    229417 4   fk_schematic_correspondence_parent_correspondence_id    FK CONSTRAINT     �   ALTER TABLE ONLY schematic_correspondences
    ADD CONSTRAINT fk_schematic_correspondence_parent_correspondence_id FOREIGN KEY (parent_schematic_correspondence_id) REFERENCES schematic_correspondences(id);
 x   ALTER TABLE ONLY public.schematic_correspondences DROP CONSTRAINT fk_schematic_correspondence_parent_correspondence_id;
       public       postgres    false    206    206    2088    2402         �           2606    229467 *   fk_super_abstract_parent_super_abstract_id    FK CONSTRAINT     �   ALTER TABLE ONLY super_abstracts
    ADD CONSTRAINT fk_super_abstract_parent_super_abstract_id FOREIGN KEY (parent_super_abstract_id) REFERENCES super_abstracts(id);
 d   ALTER TABLE ONLY public.super_abstracts DROP CONSTRAINT fk_super_abstract_parent_super_abstract_id;
       public       postgres    false    2102    211    211    2402         �           2606    229487 )   fk_super_lexical_parent_super_abstract_id    FK CONSTRAINT     �   ALTER TABLE ONLY super_lexicals
    ADD CONSTRAINT fk_super_lexical_parent_super_abstract_id FOREIGN KEY (super_lexical_parent_super_abstract_id) REFERENCES super_abstracts(id);
 b   ALTER TABLE ONLY public.super_lexicals DROP CONSTRAINT fk_super_lexical_parent_super_abstract_id;
       public       postgres    false    211    212    2102    2402         �           2606    229492 (   fk_super_lexical_parent_super_lexical_id    FK CONSTRAINT     �   ALTER TABLE ONLY super_lexicals
    ADD CONSTRAINT fk_super_lexical_parent_super_lexical_id FOREIGN KEY (parent_super_lexical_id) REFERENCES super_lexicals(id);
 a   ALTER TABLE ONLY public.super_lexicals DROP CONSTRAINT fk_super_lexical_parent_super_lexical_id;
       public       postgres    false    212    2104    212    2402         �           2606    229497 -   fk_super_lexical_parent_super_relationship_id    FK CONSTRAINT     �   ALTER TABLE ONLY super_lexicals
    ADD CONSTRAINT fk_super_lexical_parent_super_relationship_id FOREIGN KEY (parent_super_relationship_id) REFERENCES super_relationships(id);
 f   ALTER TABLE ONLY public.super_lexicals DROP CONSTRAINT fk_super_lexical_parent_super_relationship_id;
       public       postgres    false    2106    213    212    2402         �           2606    229517 3   fk_super_relationship_generalised_super_abstract_id    FK CONSTRAINT     �   ALTER TABLE ONLY super_relationships
    ADD CONSTRAINT fk_super_relationship_generalised_super_abstract_id FOREIGN KEY (super_relationship_generalised_super_abstract_id) REFERENCES super_abstracts(id);
 q   ALTER TABLE ONLY public.super_relationships DROP CONSTRAINT fk_super_relationship_generalised_super_abstract_id;
       public       postgres    false    213    2102    211    2402         �           2606    229447    fkac37d9461820cbd9    FK CONSTRAINT     �   ALTER TABLE ONLY schematic_correspondence_reconciling_expressions_1
    ADD CONSTRAINT fkac37d9461820cbd9 FOREIGN KEY (schematic_correspondence_id) REFERENCES schematic_correspondences(id);
 o   ALTER TABLE ONLY public.schematic_correspondence_reconciling_expressions_1 DROP CONSTRAINT fkac37d9461820cbd9;
       public       postgres    false    206    2088    209    2402         �           2606    229442    fkac37d94625773a15    FK CONSTRAINT     �   ALTER TABLE ONLY schematic_correspondence_reconciling_expressions_1
    ADD CONSTRAINT fkac37d94625773a15 FOREIGN KEY (reconciling_expression_id) REFERENCES reconciling_expression(id);
 o   ALTER TABLE ONLY public.schematic_correspondence_reconciling_expressions_1 DROP CONSTRAINT fkac37d94625773a15;
       public       postgres    false    209    192    2058    2402         �           2606    229457    fkac37d9471820cbd9    FK CONSTRAINT     �   ALTER TABLE ONLY schematic_correspondence_reconciling_expressions_2
    ADD CONSTRAINT fkac37d9471820cbd9 FOREIGN KEY (schematic_correspondence_id) REFERENCES schematic_correspondences(id);
 o   ALTER TABLE ONLY public.schematic_correspondence_reconciling_expressions_2 DROP CONSTRAINT fkac37d9471820cbd9;
       public       postgres    false    206    210    2088    2402         �           2606    229452    fkac37d94725773a15    FK CONSTRAINT     �   ALTER TABLE ONLY schematic_correspondence_reconciling_expressions_2
    ADD CONSTRAINT fkac37d94725773a15 FOREIGN KEY (reconciling_expression_id) REFERENCES reconciling_expression(id);
 o   ALTER TABLE ONLY public.schematic_correspondence_reconciling_expressions_2 DROP CONSTRAINT fkac37d94725773a15;
       public       postgres    false    210    2058    192    2402         �           2606    229347    fkc07cc0df85bf07b8    FK CONSTRAINT     n   ALTER TABLE ONLY role_users
    ADD CONSTRAINT fkc07cc0df85bf07b8 FOREIGN KEY (user_id) REFERENCES users(id);
 G   ALTER TABLE ONLY public.role_users DROP CONSTRAINT fkc07cc0df85bf07b8;
       public       postgres    false    2118    218    201    2402         �           2606    229342    fkc07cc0dfe09443d8    FK CONSTRAINT     n   ALTER TABLE ONLY role_users
    ADD CONSTRAINT fkc07cc0dfe09443d8 FOREIGN KEY (role_id) REFERENCES roles(id);
 G   ALTER TABLE ONLY public.role_users DROP CONSTRAINT fkc07cc0dfe09443d8;
       public       postgres    false    2076    200    201    2402         S           2606    229062    fkca18b76585bf07b8    FK CONSTRAINT     s   ALTER TABLE ONLY dataspace_users
    ADD CONSTRAINT fkca18b76585bf07b8 FOREIGN KEY (user_id) REFERENCES users(id);
 L   ALTER TABLE ONLY public.dataspace_users DROP CONSTRAINT fkca18b76585bf07b8;
       public       postgres    false    169    2118    218    2402         T           2606    229067    fkca18b765a5346195    FK CONSTRAINT     }   ALTER TABLE ONLY dataspace_users
    ADD CONSTRAINT fkca18b765a5346195 FOREIGN KEY (dataspace_id) REFERENCES dataspaces(id);
 L   ALTER TABLE ONLY public.dataspace_users DROP CONSTRAINT fkca18b765a5346195;
       public       postgres    false    2010    166    169    2402         u           2606    229232    fkd0118303a8a2e650    FK CONSTRAINT     �   ALTER TABLE ONLY query_result_mapping
    ADD CONSTRAINT fkd0118303a8a2e650 FOREIGN KEY (query_result_id) REFERENCES query_results(id);
 Q   ALTER TABLE ONLY public.query_result_mapping DROP CONSTRAINT fkd0118303a8a2e650;
       public       postgres    false    186    187    2048    2402         t           2606    229227    fkd0118303c94546c5    FK CONSTRAINT     ~   ALTER TABLE ONLY query_result_mapping
    ADD CONSTRAINT fkd0118303c94546c5 FOREIGN KEY (mapping_id) REFERENCES mappings(id);
 Q   ALTER TABLE ONLY public.query_result_mapping DROP CONSTRAINT fkd0118303c94546c5;
       public       postgres    false    187    2022    172    2402         �           2606    229317    fkd3d32ee6c94546c5    FK CONSTRAINT     �   ALTER TABLE ONLY result_instance_mapping
    ADD CONSTRAINT fkd3d32ee6c94546c5 FOREIGN KEY (mapping_id) REFERENCES mappings(id);
 T   ALTER TABLE ONLY public.result_instance_mapping DROP CONSTRAINT fkd3d32ee6c94546c5;
       public       postgres    false    2022    172    196    2402         �           2606    229312    fkd3d32ee6debf6810    FK CONSTRAINT     �   ALTER TABLE ONLY result_instance_mapping
    ADD CONSTRAINT fkd3d32ee6debf6810 FOREIGN KEY (result_instance_id) REFERENCES result_instances(id);
 T   ALTER TABLE ONLY public.result_instance_mapping DROP CONSTRAINT fkd3d32ee6debf6810;
       public       postgres    false    195    196    2064    2402         d           2606    229147    fkd87f88e056d57076    FK CONSTRAINT     �   ALTER TABLE ONLY participation_specifying_super_lexical
    ADD CONSTRAINT fkd87f88e056d57076 FOREIGN KEY (participation_in_super_relationship_id) REFERENCES participation_of_cmc_in_super_relationship(id);
 c   ALTER TABLE ONLY public.participation_specifying_super_lexical DROP CONSTRAINT fkd87f88e056d57076;
       public       postgres    false    181    2036    180    2402         e           2606    229152    fkd87f88e0900e635c    FK CONSTRAINT     �   ALTER TABLE ONLY participation_specifying_super_lexical
    ADD CONSTRAINT fkd87f88e0900e635c FOREIGN KEY (super_lexical_id) REFERENCES super_lexicals(id);
 c   ALTER TABLE ONLY public.participation_specifying_super_lexical DROP CONSTRAINT fkd87f88e0900e635c;
       public       postgres    false    212    2104    181    2402         w           2606    229242    fkd9fb03cca8a2e650    FK CONSTRAINT     �   ALTER TABLE ONLY query_result_result_instance
    ADD CONSTRAINT fkd9fb03cca8a2e650 FOREIGN KEY (query_result_id) REFERENCES query_results(id);
 Y   ALTER TABLE ONLY public.query_result_result_instance DROP CONSTRAINT fkd9fb03cca8a2e650;
       public       postgres    false    186    2048    188    2402         v           2606    229237    fkd9fb03ccdebf6810    FK CONSTRAINT     �   ALTER TABLE ONLY query_result_result_instance
    ADD CONSTRAINT fkd9fb03ccdebf6810 FOREIGN KEY (result_instance_id) REFERENCES result_instances(id);
 Y   ALTER TABLE ONLY public.query_result_result_instance DROP CONSTRAINT fkd9fb03ccdebf6810;
       public       postgres    false    2064    188    195    2402         �           2606    229547    fke9fcaf724913581e3e49da61    FK CONSTRAINT     �   ALTER TABLE ONLY scanoperator
    ADD CONSTRAINT fke9fcaf724913581e3e49da61 FOREIGN KEY (reconcilingexpression_id) REFERENCES reconciling_expression(id);
 Q   ALTER TABLE ONLY public.scanoperator DROP CONSTRAINT fke9fcaf724913581e3e49da61;
       public       postgres    false    2058    214    192    2402         �           2606    229372    fke9fcaf724913581e5e11884a    FK CONSTRAINT     �   ALTER TABLE ONLY reduceoperator
    ADD CONSTRAINT fke9fcaf724913581e5e11884a FOREIGN KEY (reconcilingexpression_id) REFERENCES reconciling_expression(id);
 S   ALTER TABLE ONLY public.reduceoperator DROP CONSTRAINT fke9fcaf724913581e5e11884a;
       public       postgres    false    202    192    2058    2402         �           2606    229397    fke9fcaf724913581e94027222    FK CONSTRAINT     �   ALTER TABLE ONLY renameoperator
    ADD CONSTRAINT fke9fcaf724913581e94027222 FOREIGN KEY (reconcilingexpression_id) REFERENCES reconciling_expression(id);
 S   ALTER TABLE ONLY public.renameoperator DROP CONSTRAINT fke9fcaf724913581e94027222;
       public       postgres    false    203    192    2058    2402         Y           2606    229092    fke9fcaf724913581e960ef40e    FK CONSTRAINT     �   ALTER TABLE ONLY joinoperator
    ADD CONSTRAINT fke9fcaf724913581e960ef40e FOREIGN KEY (reconcilingexpression_id) REFERENCES reconciling_expression(id);
 Q   ALTER TABLE ONLY public.joinoperator DROP CONSTRAINT fke9fcaf724913581e960ef40e;
       public       postgres    false    2058    192    170    2402         �           2606    229607    fke9fcaf724913581ea776797d    FK CONSTRAINT     �   ALTER TABLE ONLY typecastoperator
    ADD CONSTRAINT fke9fcaf724913581ea776797d FOREIGN KEY (reconcilingexpression_id) REFERENCES reconciling_expression(id);
 U   ALTER TABLE ONLY public.typecastoperator DROP CONSTRAINT fke9fcaf724913581ea776797d;
       public       postgres    false    2058    192    217    2402         �           2606    229572    fke9fcaf724913581ebc266f06    FK CONSTRAINT     �   ALTER TABLE ONLY setoperator
    ADD CONSTRAINT fke9fcaf724913581ebc266f06 FOREIGN KEY (reconcilingexpression_id) REFERENCES reconciling_expression(id);
 P   ALTER TABLE ONLY public.setoperator DROP CONSTRAINT fke9fcaf724913581ebc266f06;
       public       postgres    false    192    2058    215    2402         �           2606    229502    fkedb2b7f3192a9458    FK CONSTRAINT     �   ALTER TABLE ONLY super_lexicals
    ADD CONSTRAINT fkedb2b7f3192a9458 FOREIGN KEY (super_lexical_id) REFERENCES reduceoperator(id);
 K   ALTER TABLE ONLY public.super_lexicals DROP CONSTRAINT fkedb2b7f3192a9458;
       public       postgres    false    212    202    2080    2402         o           2606    229202    fkf23ef975914c355    FK CONSTRAINT     w   ALTER TABLE ONLY query_datasources
    ADD CONSTRAINT fkf23ef975914c355 FOREIGN KEY (query_id) REFERENCES queries(id);
 M   ALTER TABLE ONLY public.query_datasources DROP CONSTRAINT fkf23ef975914c355;
       public       postgres    false    185    184    2044    2402         n           2606    229197    fkf23ef97fee88a44    FK CONSTRAINT     �   ALTER TABLE ONLY query_datasources
    ADD CONSTRAINT fkf23ef97fee88a44 FOREIGN KEY (datasource_id) REFERENCES datasources(id);
 M   ALTER TABLE ONLY public.query_datasources DROP CONSTRAINT fkf23ef97fee88a44;
       public       postgres    false    2008    165    185    2402                                                                                                                                                                                                                                                                                                                                                                 2343.dat                                                                                            100600  004000  002000  00000000005 12511735575 007116  0                                                                                                    ustar00                                                                                                                                                                                                                                                        \.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           2344.dat                                                                                            100600  004000  002000  00000000005 12511735575 007117  0                                                                                                    ustar00                                                                                                                                                                                                                                                        \.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           2342.dat                                                                                            100600  004000  002000  00000000005 12511735575 007115  0                                                                                                    ustar00                                                                                                                                                                                                                                                        \.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           2345.dat                                                                                            100600  004000  002000  00000000005 12511735575 007120  0                                                                                                    ustar00                                                                                                                                                                                                                                                        \.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           2346.dat                                                                                            100600  004000  002000  00000000644 12511735575 007132  0                                                                                                    ustar00                                                                                                                                                                                                                                                        4	0	f	\N	jdbc:mysql://localhost/	This package contains the Magnatune catalogue in RDF format	com.mysql.jdbc.Driver	true	MagnatuneRDFSmpl	4321rene	./src/test/resources/schemas/magnatuneRDFSchema.xml	\N	root
136	0	f	\N	jdbc:mysql://localhost/	The package contains raw data from Jamendo website in RDF format	com.mysql.jdbc.Driver	true	JamendoRDFSmpl	4321rene	./src/test/resources/schemas/jamendoRDFSchema.xml	\N	root
\.


                                                                                            2348.dat                                                                                            100600  004000  002000  00000000005 12511735575 007123  0                                                                                                    ustar00                                                                                                                                                                                                                                                        \.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           2349.dat                                                                                            100600  004000  002000  00000000005 12511735575 007124  0                                                                                                    ustar00                                                                                                                                                                                                                                                        \.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           2350.dat                                                                                            100600  004000  002000  00000000005 12511735575 007114  0                                                                                                    ustar00                                                                                                                                                                                                                                                        \.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           2347.dat                                                                                            100600  004000  002000  00000000005 12511735575 007122  0                                                                                                    ustar00                                                                                                                                                                                                                                                        \.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           2351.dat                                                                                            100600  004000  002000  00000000005 12511735575 007115  0                                                                                                    ustar00                                                                                                                                                                                                                                                        \.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           2352.dat                                                                                            100600  004000  002000  00000000005 12511735575 007116  0                                                                                                    ustar00                                                                                                                                                                                                                                                        \.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           2353.dat                                                                                            100600  004000  002000  00000000005 12511735575 007117  0                                                                                                    ustar00                                                                                                                                                                                                                                                        \.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           2354.dat                                                                                            100600  004000  002000  00000000005 12511735575 007120  0                                                                                                    ustar00                                                                                                                                                                                                                                                        \.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           2355.dat                                                                                            100600  004000  002000  00000000005 12511735575 007121  0                                                                                                    ustar00                                                                                                                                                                                                                                                        \.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           2356.dat                                                                                            100600  004000  002000  00000000005 12511735575 007122  0                                                                                                    ustar00                                                                                                                                                                                                                                                        \.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           2358.dat                                                                                            100600  004000  002000  00000000005 12511735576 007125  0                                                                                                    ustar00                                                                                                                                                                                                                                                        \.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           2357.dat                                                                                            100600  004000  002000  00000000005 12511735576 007124  0                                                                                                    ustar00                                                                                                                                                                                                                                                        \.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           2360.dat                                                                                            100600  004000  002000  00000000005 12511735576 007116  0                                                                                                    ustar00                                                                                                                                                                                                                                                        \.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           2359.dat                                                                                            100600  004000  002000  00000000005 12511735576 007126  0                                                                                                    ustar00                                                                                                                                                                                                                                                        \.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           2361.dat                                                                                            100600  004000  002000  00000001773 12511735576 007134  0                                                                                                    ustar00                                                                                                                                                                                                                                                        99	0	f	\N	REFERENCING	9	6
100	0	f	\N	REFERENCED	42	6
104	0	f	\N	REFERENCING	18	101
105	0	f	\N	REFERENCED	57	101
109	0	f	\N	REFERENCING	18	106
110	0	f	\N	REFERENCED	78	106
114	0	f	\N	REFERENCING	33	111
115	0	f	\N	REFERENCED	9	111
119	0	f	\N	REFERENCING	33	116
120	0	f	\N	REFERENCED	78	116
124	0	f	\N	REFERENCING	45	121
125	0	f	\N	REFERENCED	33	121
129	0	f	\N	REFERENCING	45	126
130	0	f	\N	REFERENCED	57	126
134	0	f	\N	REFERENCING	78	131
135	0	f	\N	REFERENCED	57	131
252	0	f	\N	REFERENCING	150	138
253	0	f	\N	REFERENCED	204	138
257	0	f	\N	REFERENCING	150	254
258	0	f	\N	REFERENCED	231	254
262	0	f	\N	REFERENCING	150	259
263	0	f	\N	REFERENCED	186	259
267	0	f	\N	REFERENCING	150	264
268	0	f	\N	REFERENCED	225	264
272	0	f	\N	REFERENCING	177	269
273	0	f	\N	REFERENCED	144	269
277	0	f	\N	REFERENCING	177	274
278	0	f	\N	REFERENCED	231	274
282	0	f	\N	REFERENCING	204	279
283	0	f	\N	REFERENCED	150	279
287	0	f	\N	REFERENCING	231	284
288	0	f	\N	REFERENCED	141	284
292	0	f	\N	REFERENCING	231	289
293	0	f	\N	REFERENCED	192	289
\.


     2362.dat                                                                                            100600  004000  002000  00000000005 12511735576 007120  0                                                                                                    ustar00                                                                                                                                                                                                                                                        \.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           2363.dat                                                                                            100600  004000  002000  00000000005 12511735576 007121  0                                                                                                    ustar00                                                                                                                                                                                                                                                        \.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           2364.dat                                                                                            100600  004000  002000  00000024476 12511735576 007144  0                                                                                                    ustar00                                                                                                                                                                                                                                                        7	0	\N	\N	constructURI	http://purl.org/NET/c4dm/timeline.owl#onTimeLine	6
8	0	\N	\N	namespaceURI	http://purl.org/NET/c4dm/timeline.owl#	6
10	0	\N	\N	rdfTypeValue	http://www.w3.org/TR/owl-time/Interval	9
11	0	\N	\N	namespaceURI	http://www.w3.org/TR/owl-time/	9
13	0	\N	\N	constructURI	http://purl.org/NET/c4dm/timeline.owl#duration	12
14	0	\N	\N	namespaceURI	http://purl.org/NET/c4dm/timeline.owl#	12
16	0	\N	\N	constructURI	http://purl.org/NET/c4dm/timeline.owl#onTimeLine	15
17	0	\N	\N	namespaceURI	http://purl.org/NET/c4dm/timeline.owl#	15
19	0	\N	\N	rdfTypeValue	http://purl.org/ontology/mo/Record	18
20	0	\N	\N	namespaceURI	http://purl.org/ontology/mo/	18
22	0	\N	\N	constructURI	http://purl.org/dc/elements/1.1/title	21
23	0	\N	\N	namespaceURI	http://purl.org/dc/elements/1.1/	21
25	0	\N	\N	constructURI	http://purl.org/ontology/mo/publishing_location	24
26	0	\N	\N	namespaceURI	http://purl.org/ontology/mo/	24
28	0	\N	\N	constructURI	http://xmlns.com/foaf/0.1/maker	27
29	0	\N	\N	namespaceURI	http://xmlns.com/foaf/0.1/	27
31	0	\N	\N	constructURI	http://purl.org/ontology/mo/track	30
32	0	\N	\N	namespaceURI	http://purl.org/ontology/mo/	30
34	0	\N	\N	rdfTypeValue	http://purl.org/ontology/mo/Signal	33
35	0	\N	\N	namespaceURI	http://purl.org/ontology/mo/	33
37	0	\N	\N	constructURI	http://purl.org/ontology/mo/time	36
38	0	\N	\N	namespaceURI	http://purl.org/ontology/mo/	36
40	0	\N	\N	constructURI	http://purl.org/ontology/mo/published_as	39
41	0	\N	\N	namespaceURI	http://purl.org/ontology/mo/	39
43	0	\N	\N	rdfTypeValue	http://purl.org/NET/c4dm/timeline.owl#RelativeTimeLine	42
44	0	\N	\N	namespaceURI	http://purl.org/NET/c4dm/timeline.owl#	42
46	0	\N	\N	rdfTypeValue	http://purl.org/ontology/mo/Performance	45
47	0	\N	\N	namespaceURI	http://purl.org/ontology/mo/	45
49	0	\N	\N	constructURI	http://purl.org/NET/c4dm/event.owl#place	48
50	0	\N	\N	namespaceURI	http://purl.org/NET/c4dm/event.owl#	48
52	0	\N	\N	constructURI	http://purl.org/ontology/mo/recorded_as	51
53	0	\N	\N	namespaceURI	http://purl.org/ontology/mo/	51
55	0	\N	\N	constructURI	http://purl.org/ontology/mo/performer	54
56	0	\N	\N	namespaceURI	http://purl.org/ontology/mo/	54
58	0	\N	\N	rdfTypeValue	http://purl.org/ontology/mo/MusicArtist	57
59	0	\N	\N	namespaceURI	http://purl.org/ontology/mo/	57
61	0	\N	\N	constructURI	http://purl.org/vocab/bio/0.1/olb	60
62	0	\N	\N	namespaceURI	http://purl.org/vocab/bio/0.1/	60
64	0	\N	\N	constructURI	http://purl.org/dc/elements/1.1/description	63
65	0	\N	\N	namespaceURI	http://purl.org/dc/elements/1.1/	63
67	0	\N	\N	constructURI	http://xmlns.com/foaf/0.1/name	66
68	0	\N	\N	namespaceURI	http://xmlns.com/foaf/0.1/	66
70	0	\N	\N	constructURI	http://xmlns.com/foaf/0.1/img	69
71	0	\N	\N	namespaceURI	http://xmlns.com/foaf/0.1/	69
73	0	\N	\N	constructURI	http://xmlns.com/foaf/0.1/homepage	72
74	0	\N	\N	namespaceURI	http://xmlns.com/foaf/0.1/	72
76	0	\N	\N	constructURI	http://xmlns.com/foaf/0.1/based_near	75
77	0	\N	\N	namespaceURI	http://xmlns.com/foaf/0.1/	75
79	0	\N	\N	rdfTypeValue	http://purl.org/ontology/mo/Track	78
80	0	\N	\N	namespaceURI	http://purl.org/ontology/mo/	78
82	0	\N	\N	constructURI	http://purl.org/dc/elements/1.1/title	81
83	0	\N	\N	namespaceURI	http://purl.org/dc/elements/1.1/	81
85	0	\N	\N	constructURI	http://purl.org/dc/elements/1.1/created	84
86	0	\N	\N	namespaceURI	http://purl.org/dc/elements/1.1/	84
88	0	\N	\N	constructURI	http://purl.org/ontology/mo/paid_download	87
89	0	\N	\N	namespaceURI	http://purl.org/ontology/mo/	87
91	0	\N	\N	constructURI	http://purl.org/ontology/mo/track_number	90
92	0	\N	\N	namespaceURI	http://purl.org/ontology/mo/	90
94	0	\N	\N	constructURI	http://purl.org/ontology/mo/available_as	93
95	0	\N	\N	namespaceURI	http://purl.org/ontology/mo/	93
97	0	\N	\N	constructURI	http://xmlns.com/foaf/0.1/maker	96
98	0	\N	\N	namespaceURI	http://xmlns.com/foaf/0.1/	96
102	0	\N	\N	constructURI	http://xmlns.com/foaf/0.1/maker	101
103	0	\N	\N	namespaceURI	http://xmlns.com/foaf/0.1/	101
107	0	\N	\N	constructURI	http://purl.org/ontology/mo/track	106
108	0	\N	\N	namespaceURI	http://purl.org/ontology/mo/	106
112	0	\N	\N	constructURI	http://purl.org/ontology/mo/time	111
113	0	\N	\N	namespaceURI	http://purl.org/ontology/mo/	111
117	0	\N	\N	constructURI	http://purl.org/ontology/mo/published_as	116
118	0	\N	\N	namespaceURI	http://purl.org/ontology/mo/	116
122	0	\N	\N	constructURI	http://purl.org/ontology/mo/recorded_as	121
123	0	\N	\N	namespaceURI	http://purl.org/ontology/mo/	121
127	0	\N	\N	constructURI	http://purl.org/ontology/mo/performer	126
128	0	\N	\N	namespaceURI	http://purl.org/ontology/mo/	126
132	0	\N	\N	constructURI	http://xmlns.com/foaf/0.1/maker	131
133	0	\N	\N	namespaceURI	http://xmlns.com/foaf/0.1/	131
139	0	\N	\N	constructURI	http://xmlns.com/foaf/0.1/maker	138
140	0	\N	\N	namespaceURI	http://xmlns.com/foaf/0.1/	138
142	0	\N	\N	rdfTypeValue	http://xmlns.com/foaf/0.1/Document	141
143	0	\N	\N	namespaceURI	http://xmlns.com/foaf/0.1/	141
145	0	\N	\N	rdfTypeValue	http://www.w3.org/2006/time#Interval	144
146	0	\N	\N	namespaceURI	http://www.w3.org/2006/time#	144
148	0	\N	\N	constructURI	http://purl.org/NET/c4dm/timeline.owl#onTimeLine	147
149	0	\N	\N	namespaceURI	http://purl.org/NET/c4dm/timeline.owl#	147
151	0	\N	\N	rdfTypeValue	http://purl.org/ontology/mo/Record	150
152	0	\N	\N	namespaceURI	http://purl.org/ontology/mo/	150
154	0	\N	\N	constructURI	http://purl.org/dc/elements/1.1/title	153
155	0	\N	\N	namespaceURI	http://purl.org/dc/elements/1.1/	153
157	0	\N	\N	constructURI	http://purl.org/dc/elements/1.1/description	156
158	0	\N	\N	namespaceURI	http://purl.org/dc/elements/1.1/	156
160	0	\N	\N	constructURI	http://purl.org/ontology/mo/image	159
161	0	\N	\N	namespaceURI	http://purl.org/ontology/mo/	159
163	0	\N	\N	constructURI	http://purl.org/dc/elements/1.1/date	162
164	0	\N	\N	namespaceURI	http://purl.org/dc/elements/1.1/	162
166	0	\N	\N	constructURI	http://xmlns.com/foaf/0.1/maker	165
167	0	\N	\N	namespaceURI	http://xmlns.com/foaf/0.1/	165
169	0	\N	\N	constructURI	http://purl.org/ontology/mo/track	168
170	0	\N	\N	namespaceURI	http://purl.org/ontology/mo/	168
172	0	\N	\N	constructURI	http://www.holygoat.co.uk/owl/redwood/0.1/tags/taggedWithTag	171
173	0	\N	\N	namespaceURI	http://www.holygoat.co.uk/owl/redwood/0.1/tags/	171
175	0	\N	\N	constructURI	http://purl.org/ontology/mo/available_as	174
176	0	\N	\N	namespaceURI	http://purl.org/ontology/mo/	174
178	0	\N	\N	rdfTypeValue	http://purl.org/ontology/mo/Signal	177
179	0	\N	\N	namespaceURI	http://purl.org/ontology/mo/	177
181	0	\N	\N	constructURI	http://purl.org/ontology/mo/time	180
182	0	\N	\N	namespaceURI	http://purl.org/ontology/mo/	180
184	0	\N	\N	constructURI	http://purl.org/ontology/mo/published_as	183
185	0	\N	\N	namespaceURI	http://purl.org/ontology/mo/	183
187	0	\N	\N	rdfTypeValue	http://www.holygoat.co.uk/owl/redwood/0.1/tags/Tag	186
188	0	\N	\N	namespaceURI	http://www.holygoat.co.uk/owl/redwood/0.1/tags/	186
190	0	\N	\N	constructURI	http://www.holygoat.co.uk/owl/redwood/0.1/tags/tagName	189
191	0	\N	\N	namespaceURI	http://www.holygoat.co.uk/owl/redwood/0.1/tags/	189
193	0	\N	\N	rdfTypeValue	http://purl.org/ontology/mo/Playlist	192
194	0	\N	\N	namespaceURI	http://purl.org/ontology/mo/	192
196	0	\N	\N	constructURI	http://purl.org/dc/elements/1.1/format	195
197	0	\N	\N	namespaceURI	http://purl.org/dc/elements/1.1/	195
199	0	\N	\N	rdfTypeValue	http://purl.org/ontology/mo/Torrent	198
200	0	\N	\N	namespaceURI	http://purl.org/ontology/mo/	198
202	0	\N	\N	constructURI	http://purl.org/dc/elements/1.1/format	201
203	0	\N	\N	namespaceURI	http://purl.org/dc/elements/1.1/	201
205	0	\N	\N	rdfTypeValue	http://purl.org/ontology/mo/MusicArtist	204
206	0	\N	\N	namespaceURI	http://purl.org/ontology/mo/	204
208	0	\N	\N	constructURI	http://xmlns.com/foaf/0.1/name	207
209	0	\N	\N	namespaceURI	http://xmlns.com/foaf/0.1/	207
211	0	\N	\N	constructURI	http://xmlns.com/foaf/0.1/img	210
212	0	\N	\N	namespaceURI	http://xmlns.com/foaf/0.1/	210
214	0	\N	\N	constructURI	http://purl.org/ontology/mo/biography	213
215	0	\N	\N	namespaceURI	http://purl.org/ontology/mo/	213
217	0	\N	\N	constructURI	http://xmlns.com/foaf/0.1/homepage	216
218	0	\N	\N	namespaceURI	http://xmlns.com/foaf/0.1/	216
220	0	\N	\N	constructURI	http://xmlns.com/foaf/0.1/based_near	219
221	0	\N	\N	namespaceURI	http://xmlns.com/foaf/0.1/	219
223	0	\N	\N	constructURI	http://xmlns.com/foaf/0.1/made	222
224	0	\N	\N	namespaceURI	http://xmlns.com/foaf/0.1/	222
226	0	\N	\N	rdfTypeValue	http://purl.org/ontology/mo/ED2K	225
227	0	\N	\N	namespaceURI	http://purl.org/ontology/mo/	225
229	0	\N	\N	constructURI	http://purl.org/dc/elements/1.1/format	228
230	0	\N	\N	namespaceURI	http://purl.org/dc/elements/1.1/	228
232	0	\N	\N	rdfTypeValue	http://purl.org/ontology/mo/Track	231
233	0	\N	\N	namespaceURI	http://purl.org/ontology/mo/	231
235	0	\N	\N	constructURI	http://purl.org/dc/elements/1.1/title	234
236	0	\N	\N	namespaceURI	http://purl.org/dc/elements/1.1/	234
238	0	\N	\N	constructURI	http://purl.org/ontology/mo/track_number	237
239	0	\N	\N	namespaceURI	http://purl.org/ontology/mo/	237
241	0	\N	\N	constructURI	http://purl.org/ontology/mo/license	240
242	0	\N	\N	namespaceURI	http://purl.org/ontology/mo/	240
244	0	\N	\N	constructURI	http://purl.org/ontology/mo/available_as	243
245	0	\N	\N	namespaceURI	http://purl.org/ontology/mo/	243
247	0	\N	\N	rdfTypeValue	http://purl.org/ontology/mo/Lyrics	246
248	0	\N	\N	namespaceURI	http://purl.org/ontology/mo/	246
250	0	\N	\N	constructURI	http://purl.org/ontology/mo/text	249
251	0	\N	\N	namespaceURI	http://purl.org/ontology/mo/	249
255	0	\N	\N	constructURI	http://purl.org/ontology/mo/track	254
256	0	\N	\N	namespaceURI	http://purl.org/ontology/mo/	254
260	0	\N	\N	constructURI	http://www.holygoat.co.uk/owl/redwood/0.1/tags/taggedWithTag	259
261	0	\N	\N	namespaceURI	http://www.holygoat.co.uk/owl/redwood/0.1/tags/	259
265	0	\N	\N	constructURI	http://purl.org/ontology/mo/available_as	264
266	0	\N	\N	namespaceURI	http://purl.org/ontology/mo/	264
270	0	\N	\N	constructURI	http://purl.org/ontology/mo/time	269
271	0	\N	\N	namespaceURI	http://purl.org/ontology/mo/	269
275	0	\N	\N	constructURI	http://purl.org/ontology/mo/published_as	274
276	0	\N	\N	namespaceURI	http://purl.org/ontology/mo/	274
280	0	\N	\N	constructURI	http://xmlns.com/foaf/0.1/made	279
281	0	\N	\N	namespaceURI	http://xmlns.com/foaf/0.1/	279
285	0	\N	\N	constructURI	http://purl.org/ontology/mo/license	284
286	0	\N	\N	namespaceURI	http://purl.org/ontology/mo/	284
290	0	\N	\N	constructURI	http://purl.org/ontology/mo/available_as	289
291	0	\N	\N	namespaceURI	http://purl.org/ontology/mo/	289
\.


                                                                                                                                                                                                  2365.dat                                                                                            100600  004000  002000  00000000005 12511735576 007123  0                                                                                                    ustar00                                                                                                                                                                                                                                                        \.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           2366.dat                                                                                            100600  004000  002000  00000000005 12511735576 007124  0                                                                                                    ustar00                                                                                                                                                                                                                                                        \.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           2368.dat                                                                                            100600  004000  002000  00000000005 12511735576 007126  0                                                                                                    ustar00                                                                                                                                                                                                                                                        \.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           2369.dat                                                                                            100600  004000  002000  00000000005 12511735576 007127  0                                                                                                    ustar00                                                                                                                                                                                                                                                        \.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           2370.dat                                                                                            100600  004000  002000  00000000005 12511735576 007117  0                                                                                                    ustar00                                                                                                                                                                                                                                                        \.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           2367.dat                                                                                            100600  004000  002000  00000000005 12511735576 007125  0                                                                                                    ustar00                                                                                                                                                                                                                                                        \.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           2371.dat                                                                                            100600  004000  002000  00000000005 12511735576 007120  0                                                                                                    ustar00                                                                                                                                                                                                                                                        \.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           2372.dat                                                                                            100600  004000  002000  00000000005 12511735576 007121  0                                                                                                    ustar00                                                                                                                                                                                                                                                        \.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           2373.dat                                                                                            100600  004000  002000  00000000005 12511735576 007122  0                                                                                                    ustar00                                                                                                                                                                                                                                                        \.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           2383.dat                                                                                            100600  004000  002000  00000000005 12511735576 007123  0                                                                                                    ustar00                                                                                                                                                                                                                                                        \.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           2384.dat                                                                                            100600  004000  002000  00000000005 12511735576 007124  0                                                                                                    ustar00                                                                                                                                                                                                                                                        \.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           2375.dat                                                                                            100600  004000  002000  00000000005 12511735576 007124  0                                                                                                    ustar00                                                                                                                                                                                                                                                        \.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           2374.dat                                                                                            100600  004000  002000  00000000005 12511735576 007123  0                                                                                                    ustar00                                                                                                                                                                                                                                                        \.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           2377.dat                                                                                            100600  004000  002000  00000000005 12511735576 007126  0                                                                                                    ustar00                                                                                                                                                                                                                                                        \.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           2378.dat                                                                                            100600  004000  002000  00000000005 12511735576 007127  0                                                                                                    ustar00                                                                                                                                                                                                                                                        \.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           2376.dat                                                                                            100600  004000  002000  00000000005 12511735576 007125  0                                                                                                    ustar00                                                                                                                                                                                                                                                        \.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           2380.dat                                                                                            100600  004000  002000  00000000005 12511735576 007120  0                                                                                                    ustar00                                                                                                                                                                                                                                                        \.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           2379.dat                                                                                            100600  004000  002000  00000000005 12511735576 007130  0                                                                                                    ustar00                                                                                                                                                                                                                                                        \.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           2382.dat                                                                                            100600  004000  002000  00000000005 12511735576 007122  0                                                                                                    ustar00                                                                                                                                                                                                                                                        \.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           2381.dat                                                                                            100600  004000  002000  00000000005 12511735576 007121  0                                                                                                    ustar00                                                                                                                                                                                                                                                        \.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           2385.dat                                                                                            100600  004000  002000  00000000005 12511735576 007125  0                                                                                                    ustar00                                                                                                                                                                                                                                                        \.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           2395.dat                                                                                            100600  004000  002000  00000000005 12511735576 007126  0                                                                                                    ustar00                                                                                                                                                                                                                                                        \.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           2386.dat                                                                                            100600  004000  002000  00000000115 12511735576 007130  0                                                                                                    ustar00                                                                                                                                                                                                                                                        5	25	f	\N	RDF	magnatuneRDFSchema	4
137	28	f	\N	RDF	jamendoRDFSchema	136
\.


                                                                                                                                                                                                                                                                                                                                                                                                                                                   2390.dat                                                                                            100600  004000  002000  00000000005 12511735576 007121  0                                                                                                    ustar00                                                                                                                                                                                                                                                        \.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           2391.dat                                                                                            100600  004000  002000  00000000005 12511735576 007122  0                                                                                                    ustar00                                                                                                                                                                                                                                                        \.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           2387.dat                                                                                            100600  004000  002000  00000000005 12511735576 007127  0                                                                                                    ustar00                                                                                                                                                                                                                                                        \.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           2388.dat                                                                                            100600  004000  002000  00000000005 12511735576 007130  0                                                                                                    ustar00                                                                                                                                                                                                                                                        \.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           2389.dat                                                                                            100600  004000  002000  00000000005 12511735576 007131  0                                                                                                    ustar00                                                                                                                                                                                                                                                        \.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           2396.dat                                                                                            100600  004000  002000  00000000005 12511735576 007127  0                                                                                                    ustar00                                                                                                                                                                                                                                                        \.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           2392.dat                                                                                            100600  004000  002000  00000002060 12511735576 007126  0                                                                                                    ustar00                                                                                                                                                                                                                                                        42	1	f	\N	t	f	RelativeTimeLine	SUPER_ABSTRACT	5	0	\N	RDF_CLASS	\N
18	2	f	\N	t	f	Record	SUPER_ABSTRACT	5	0	\N	RDF_CLASS	\N
9	2	f	\N	t	f	Interval	SUPER_ABSTRACT	5	0	\N	RDF_CLASS	\N
33	3	f	\N	t	f	Signal	SUPER_ABSTRACT	5	0	\N	RDF_CLASS	\N
45	2	f	\N	t	f	Performance	SUPER_ABSTRACT	5	0	\N	RDF_CLASS	\N
57	3	f	\N	t	f	MusicArtist	SUPER_ABSTRACT	5	0	\N	RDF_CLASS	\N
78	3	f	\N	t	f	Track	SUPER_ABSTRACT	5	0	\N	RDF_CLASS	\N
198	0	f	\N	t	f	Torrent	SUPER_ABSTRACT	137	0	\N	RDF_CLASS	\N
246	0	f	\N	t	f	Lyrics	SUPER_ABSTRACT	137	0	\N	RDF_CLASS	\N
186	1	f	\N	t	f	Tag	SUPER_ABSTRACT	137	0	\N	RDF_CLASS	\N
225	1	f	\N	t	f	ED2K	SUPER_ABSTRACT	137	0	\N	RDF_CLASS	\N
144	1	f	\N	t	f	Interval	SUPER_ABSTRACT	137	0	\N	RDF_CLASS	\N
177	2	f	\N	t	f	Signal	SUPER_ABSTRACT	137	0	\N	RDF_CLASS	\N
150	5	f	\N	t	f	Record	SUPER_ABSTRACT	137	0	\N	RDF_CLASS	\N
204	2	f	\N	t	f	MusicArtist	SUPER_ABSTRACT	137	0	\N	RDF_CLASS	\N
141	1	f	\N	t	f	Document	SUPER_ABSTRACT	137	0	\N	RDF_CLASS	\N
192	1	f	\N	t	f	Playlist	SUPER_ABSTRACT	137	0	\N	RDF_CLASS	\N
231	4	f	\N	t	f	Track	SUPER_ABSTRACT	137	0	\N	RDF_CLASS	\N
\.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                2393.dat                                                                                            100600  004000  002000  00000010462 12511735576 007134  0                                                                                                    ustar00                                                                                                                                                                                                                                                        12	0	f	\N	t	f	duration	SUPER_LEXICAL	5	STRING	f	t	\N	0	\N	RDF_PROPERTY	0	9	\N	\N	\N	\N
15	0	f	\N	t	f	onTimeLine	SUPER_LEXICAL	5	STRING	f	t	\N	0	\N	RDF_PROPERTY	0	9	\N	\N	\N	\N
21	0	f	\N	t	f	title	SUPER_LEXICAL	5	STRING	f	t	\N	0	\N	RDF_PROPERTY	0	18	\N	\N	\N	\N
24	0	f	\N	t	f	publishing_location	SUPER_LEXICAL	5	STRING	f	t	\N	0	\N	RDF_PROPERTY	0	18	\N	\N	\N	\N
27	0	f	\N	t	f	maker	SUPER_LEXICAL	5	STRING	f	t	\N	0	\N	RDF_PROPERTY	0	18	\N	\N	\N	\N
30	0	f	\N	t	f	track	SUPER_LEXICAL	5	STRING	f	t	\N	0	\N	RDF_PROPERTY	0	18	\N	\N	\N	\N
36	0	f	\N	t	f	time	SUPER_LEXICAL	5	STRING	f	t	\N	0	\N	RDF_PROPERTY	0	33	\N	\N	\N	\N
39	0	f	\N	t	f	published_as	SUPER_LEXICAL	5	STRING	f	t	\N	0	\N	RDF_PROPERTY	0	33	\N	\N	\N	\N
48	0	f	\N	t	f	place	SUPER_LEXICAL	5	STRING	f	t	\N	0	\N	RDF_PROPERTY	0	45	\N	\N	\N	\N
51	0	f	\N	t	f	recorded_as	SUPER_LEXICAL	5	STRING	f	t	\N	0	\N	RDF_PROPERTY	0	45	\N	\N	\N	\N
54	0	f	\N	t	f	performer	SUPER_LEXICAL	5	STRING	f	t	\N	0	\N	RDF_PROPERTY	0	45	\N	\N	\N	\N
60	0	f	\N	t	f	olb	SUPER_LEXICAL	5	STRING	f	t	\N	0	\N	RDF_PROPERTY	0	57	\N	\N	\N	\N
63	0	f	\N	t	f	description	SUPER_LEXICAL	5	STRING	f	t	\N	0	\N	RDF_PROPERTY	0	57	\N	\N	\N	\N
66	0	f	\N	t	f	name	SUPER_LEXICAL	5	STRING	f	t	\N	0	\N	RDF_PROPERTY	0	57	\N	\N	\N	\N
69	0	f	\N	t	f	img	SUPER_LEXICAL	5	STRING	f	t	\N	0	\N	RDF_PROPERTY	0	57	\N	\N	\N	\N
72	0	f	\N	t	f	homepage	SUPER_LEXICAL	5	STRING	f	t	\N	0	\N	RDF_PROPERTY	0	57	\N	\N	\N	\N
75	0	f	\N	t	f	based_near	SUPER_LEXICAL	5	STRING	f	t	\N	0	\N	RDF_PROPERTY	0	57	\N	\N	\N	\N
81	0	f	\N	t	f	title	SUPER_LEXICAL	5	STRING	f	t	\N	0	\N	RDF_PROPERTY	0	78	\N	\N	\N	\N
84	0	f	\N	t	f	created	SUPER_LEXICAL	5	STRING	f	t	\N	0	\N	RDF_PROPERTY	0	78	\N	\N	\N	\N
87	0	f	\N	t	f	paid_download	SUPER_LEXICAL	5	STRING	f	t	\N	0	\N	RDF_PROPERTY	0	78	\N	\N	\N	\N
90	0	f	\N	t	f	track_number	SUPER_LEXICAL	5	STRING	f	t	\N	0	\N	RDF_PROPERTY	0	78	\N	\N	\N	\N
93	0	f	\N	t	f	available_as	SUPER_LEXICAL	5	STRING	f	t	\N	0	\N	RDF_PROPERTY	0	78	\N	\N	\N	\N
96	0	f	\N	t	f	maker	SUPER_LEXICAL	5	STRING	f	t	\N	0	\N	RDF_PROPERTY	0	78	\N	\N	\N	\N
147	0	f	\N	t	f	onTimeLine	SUPER_LEXICAL	137	STRING	f	t	\N	0	\N	RDF_PROPERTY	0	144	\N	\N	\N	\N
153	0	f	\N	t	f	title	SUPER_LEXICAL	137	STRING	f	t	\N	0	\N	RDF_PROPERTY	0	150	\N	\N	\N	\N
156	0	f	\N	t	f	description	SUPER_LEXICAL	137	STRING	f	t	\N	0	\N	RDF_PROPERTY	0	150	\N	\N	\N	\N
159	0	f	\N	t	f	image	SUPER_LEXICAL	137	STRING	f	t	\N	0	\N	RDF_PROPERTY	0	150	\N	\N	\N	\N
162	0	f	\N	t	f	date	SUPER_LEXICAL	137	STRING	f	t	\N	0	\N	RDF_PROPERTY	0	150	\N	\N	\N	\N
165	0	f	\N	t	f	maker	SUPER_LEXICAL	137	STRING	f	t	\N	0	\N	RDF_PROPERTY	0	150	\N	\N	\N	\N
168	0	f	\N	t	f	track	SUPER_LEXICAL	137	STRING	f	t	\N	0	\N	RDF_PROPERTY	0	150	\N	\N	\N	\N
171	0	f	\N	t	f	taggedWithTag	SUPER_LEXICAL	137	STRING	f	t	\N	0	\N	RDF_PROPERTY	0	150	\N	\N	\N	\N
174	0	f	\N	t	f	available_as	SUPER_LEXICAL	137	STRING	f	t	\N	0	\N	RDF_PROPERTY	0	150	\N	\N	\N	\N
180	0	f	\N	t	f	time	SUPER_LEXICAL	137	STRING	f	t	\N	0	\N	RDF_PROPERTY	0	177	\N	\N	\N	\N
183	0	f	\N	t	f	published_as	SUPER_LEXICAL	137	STRING	f	t	\N	0	\N	RDF_PROPERTY	0	177	\N	\N	\N	\N
189	0	f	\N	t	f	tagName	SUPER_LEXICAL	137	STRING	f	t	\N	0	\N	RDF_PROPERTY	0	186	\N	\N	\N	\N
195	0	f	\N	t	f	format	SUPER_LEXICAL	137	STRING	f	t	\N	0	\N	RDF_PROPERTY	0	192	\N	\N	\N	\N
201	0	f	\N	t	f	format	SUPER_LEXICAL	137	STRING	f	t	\N	0	\N	RDF_PROPERTY	0	198	\N	\N	\N	\N
207	0	f	\N	t	f	name	SUPER_LEXICAL	137	STRING	f	t	\N	0	\N	RDF_PROPERTY	0	204	\N	\N	\N	\N
210	0	f	\N	t	f	img	SUPER_LEXICAL	137	STRING	f	t	\N	0	\N	RDF_PROPERTY	0	204	\N	\N	\N	\N
213	0	f	\N	t	f	biography	SUPER_LEXICAL	137	STRING	f	t	\N	0	\N	RDF_PROPERTY	0	204	\N	\N	\N	\N
216	0	f	\N	t	f	homepage	SUPER_LEXICAL	137	STRING	f	t	\N	0	\N	RDF_PROPERTY	0	204	\N	\N	\N	\N
219	0	f	\N	t	f	based_near	SUPER_LEXICAL	137	STRING	f	t	\N	0	\N	RDF_PROPERTY	0	204	\N	\N	\N	\N
222	0	f	\N	t	f	made	SUPER_LEXICAL	137	STRING	f	t	\N	0	\N	RDF_PROPERTY	0	204	\N	\N	\N	\N
228	0	f	\N	t	f	format	SUPER_LEXICAL	137	STRING	f	t	\N	0	\N	RDF_PROPERTY	0	225	\N	\N	\N	\N
234	0	f	\N	t	f	title	SUPER_LEXICAL	137	STRING	f	t	\N	0	\N	RDF_PROPERTY	0	231	\N	\N	\N	\N
237	0	f	\N	t	f	track_number	SUPER_LEXICAL	137	STRING	f	t	\N	0	\N	RDF_PROPERTY	0	231	\N	\N	\N	\N
240	0	f	\N	t	f	license	SUPER_LEXICAL	137	STRING	f	t	\N	0	\N	RDF_PROPERTY	0	231	\N	\N	\N	\N
243	0	f	\N	t	f	available_as	SUPER_LEXICAL	137	STRING	f	t	\N	0	\N	RDF_PROPERTY	0	231	\N	\N	\N	\N
249	0	f	\N	t	f	text	SUPER_LEXICAL	137	STRING	f	t	\N	0	\N	RDF_PROPERTY	0	246	\N	\N	\N	\N
\.


                                                                                                                                                                                                              2394.dat                                                                                            100600  004000  002000  00000002230 12511735576 007127  0                                                                                                    ustar00                                                                                                                                                                                                                                                        6	1	f	\N	t	f	onTimeLine	SUPER_RELATIONSHIP	5	\N	RDF_PREDICATE	\N	\N
101	1	f	\N	t	f	maker	SUPER_RELATIONSHIP	5	\N	RDF_PREDICATE	\N	\N
106	1	f	\N	t	f	track	SUPER_RELATIONSHIP	5	\N	RDF_PREDICATE	\N	\N
111	1	f	\N	t	f	time	SUPER_RELATIONSHIP	5	\N	RDF_PREDICATE	\N	\N
116	1	f	\N	t	f	published_as	SUPER_RELATIONSHIP	5	\N	RDF_PREDICATE	\N	\N
121	1	f	\N	t	f	recorded_as	SUPER_RELATIONSHIP	5	\N	RDF_PREDICATE	\N	\N
126	1	f	\N	t	f	performer	SUPER_RELATIONSHIP	5	\N	RDF_PREDICATE	\N	\N
131	1	f	\N	t	f	maker	SUPER_RELATIONSHIP	5	\N	RDF_PREDICATE	\N	\N
138	1	f	\N	t	f	maker	SUPER_RELATIONSHIP	137	\N	RDF_PREDICATE	\N	\N
254	1	f	\N	t	f	track	SUPER_RELATIONSHIP	137	\N	RDF_PREDICATE	\N	\N
259	1	f	\N	t	f	taggedWithTag	SUPER_RELATIONSHIP	137	\N	RDF_PREDICATE	\N	\N
264	1	f	\N	t	f	available_as	SUPER_RELATIONSHIP	137	\N	RDF_PREDICATE	\N	\N
269	1	f	\N	t	f	time	SUPER_RELATIONSHIP	137	\N	RDF_PREDICATE	\N	\N
274	1	f	\N	t	f	published_as	SUPER_RELATIONSHIP	137	\N	RDF_PREDICATE	\N	\N
279	1	f	\N	t	f	made	SUPER_RELATIONSHIP	137	\N	RDF_PREDICATE	\N	\N
284	1	f	\N	t	f	license	SUPER_RELATIONSHIP	137	\N	RDF_PREDICATE	\N	\N
289	1	f	\N	t	f	available_as	SUPER_RELATIONSHIP	137	\N	RDF_PREDICATE	\N	\N
\.


                                                                                                                                                                                                                                                                                                                                                                        2397.dat                                                                                            100600  004000  002000  00000000005 12511735576 007130  0                                                                                                    ustar00                                                                                                                                                                                                                                                        \.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           2398.dat                                                                                            100600  004000  002000  00000000005 12511735576 007131  0                                                                                                    ustar00                                                                                                                                                                                                                                                        \.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           2399.dat                                                                                            100600  004000  002000  00000000005 12511735576 007132  0                                                                                                    ustar00                                                                                                                                                                                                                                                        \.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           restore.sql                                                                                         100600  004000  002000  00000341627 12511735576 010260  0                                                                                                    ustar00                                                                                                                                                                                                                                                        --
-- NOTE:
--
-- File paths need to be edited. Search for $$PATH$$ and
-- replace it with the path to the directory containing
-- the extracted data files.
--
--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = public, pg_catalog;

ALTER TABLE ONLY public.query_datasources DROP CONSTRAINT fkf23ef97fee88a44;
ALTER TABLE ONLY public.query_datasources DROP CONSTRAINT fkf23ef975914c355;
ALTER TABLE ONLY public.super_lexicals DROP CONSTRAINT fkedb2b7f3192a9458;
ALTER TABLE ONLY public.setoperator DROP CONSTRAINT fke9fcaf724913581ebc266f06;
ALTER TABLE ONLY public.typecastoperator DROP CONSTRAINT fke9fcaf724913581ea776797d;
ALTER TABLE ONLY public.joinoperator DROP CONSTRAINT fke9fcaf724913581e960ef40e;
ALTER TABLE ONLY public.renameoperator DROP CONSTRAINT fke9fcaf724913581e94027222;
ALTER TABLE ONLY public.reduceoperator DROP CONSTRAINT fke9fcaf724913581e5e11884a;
ALTER TABLE ONLY public.scanoperator DROP CONSTRAINT fke9fcaf724913581e3e49da61;
ALTER TABLE ONLY public.query_result_result_instance DROP CONSTRAINT fkd9fb03ccdebf6810;
ALTER TABLE ONLY public.query_result_result_instance DROP CONSTRAINT fkd9fb03cca8a2e650;
ALTER TABLE ONLY public.participation_specifying_super_lexical DROP CONSTRAINT fkd87f88e0900e635c;
ALTER TABLE ONLY public.participation_specifying_super_lexical DROP CONSTRAINT fkd87f88e056d57076;
ALTER TABLE ONLY public.result_instance_mapping DROP CONSTRAINT fkd3d32ee6debf6810;
ALTER TABLE ONLY public.result_instance_mapping DROP CONSTRAINT fkd3d32ee6c94546c5;
ALTER TABLE ONLY public.query_result_mapping DROP CONSTRAINT fkd0118303c94546c5;
ALTER TABLE ONLY public.query_result_mapping DROP CONSTRAINT fkd0118303a8a2e650;
ALTER TABLE ONLY public.dataspace_users DROP CONSTRAINT fkca18b765a5346195;
ALTER TABLE ONLY public.dataspace_users DROP CONSTRAINT fkca18b76585bf07b8;
ALTER TABLE ONLY public.role_users DROP CONSTRAINT fkc07cc0dfe09443d8;
ALTER TABLE ONLY public.role_users DROP CONSTRAINT fkc07cc0df85bf07b8;
ALTER TABLE ONLY public.schematic_correspondence_reconciling_expressions_2 DROP CONSTRAINT fkac37d94725773a15;
ALTER TABLE ONLY public.schematic_correspondence_reconciling_expressions_2 DROP CONSTRAINT fkac37d9471820cbd9;
ALTER TABLE ONLY public.schematic_correspondence_reconciling_expressions_1 DROP CONSTRAINT fkac37d94625773a15;
ALTER TABLE ONLY public.schematic_correspondence_reconciling_expressions_1 DROP CONSTRAINT fkac37d9461820cbd9;
ALTER TABLE ONLY public.super_relationships DROP CONSTRAINT fk_super_relationship_generalised_super_abstract_id;
ALTER TABLE ONLY public.super_lexicals DROP CONSTRAINT fk_super_lexical_parent_super_relationship_id;
ALTER TABLE ONLY public.super_lexicals DROP CONSTRAINT fk_super_lexical_parent_super_lexical_id;
ALTER TABLE ONLY public.super_lexicals DROP CONSTRAINT fk_super_lexical_parent_super_abstract_id;
ALTER TABLE ONLY public.super_abstracts DROP CONSTRAINT fk_super_abstract_parent_super_abstract_id;
ALTER TABLE ONLY public.schematic_correspondences DROP CONSTRAINT fk_schematic_correspondence_parent_correspondence_id;
ALTER TABLE ONLY public.schemas DROP CONSTRAINT fk_schema_datasource_id;
ALTER TABLE ONLY public.scanoperator DROP CONSTRAINT fk_scan_super_abstract_id;
ALTER TABLE ONLY public.sample_kde DROP CONSTRAINT fk_sample_estimator_id;
ALTER TABLE ONLY public.result_instances DROP CONSTRAINT fk_result_instance_result_type_id;
ALTER TABLE ONLY public.result_instances DROP CONSTRAINT fk_result_instance_query_id;
ALTER TABLE ONLY public.reconciling_expression DROP CONSTRAINT fk_reconciling_expression_target_super_abstract_id;
ALTER TABLE ONLY public.reconciling_expression DROP CONSTRAINT fk_reconciling_expression_join_pred2_id;
ALTER TABLE ONLY public.reconciling_expression DROP CONSTRAINT fk_reconciling_expression_join_pred1_id;
ALTER TABLE ONLY public.queries DROP CONSTRAINT fk_query_user_id;
ALTER TABLE ONLY public.query_results DROP CONSTRAINT fk_query_result_result_type_id;
ALTER TABLE ONLY public.query_results DROP CONSTRAINT fk_query_result_query_id;
ALTER TABLE ONLY public.query_results DROP CONSTRAINT fk_query_result_dataspace_id;
ALTER TABLE ONLY public.queries DROP CONSTRAINT fk_query_dataspace_id;
ALTER TABLE ONLY public.predicates DROP CONSTRAINT fk_predicate_super_lexical2_id;
ALTER TABLE ONLY public.predicates DROP CONSTRAINT fk_predicate_super_lexical1_id;
ALTER TABLE ONLY public.participation_of_cmc_in_super_relationship DROP CONSTRAINT fk_participation_super_relationship_id;
ALTER TABLE ONLY public.parameters DROP CONSTRAINT fk_parameter_schematic_correspondence_id;
ALTER TABLE ONLY public.setoperator DROP CONSTRAINT fk_operator_mapping_idbc266f06;
ALTER TABLE ONLY public.typecastoperator DROP CONSTRAINT fk_operator_mapping_ida776797d;
ALTER TABLE ONLY public.joinoperator DROP CONSTRAINT fk_operator_mapping_id960ef40e;
ALTER TABLE ONLY public.renameoperator DROP CONSTRAINT fk_operator_mapping_id94027222;
ALTER TABLE ONLY public.reduceoperator DROP CONSTRAINT fk_operator_mapping_id5e11884a;
ALTER TABLE ONLY public.scanoperator DROP CONSTRAINT fk_operator_mapping_id3e49da61;
ALTER TABLE ONLY public.setoperator DROP CONSTRAINT fk_operator_data_source_idbc266f06;
ALTER TABLE ONLY public.typecastoperator DROP CONSTRAINT fk_operator_data_source_ida776797d;
ALTER TABLE ONLY public.joinoperator DROP CONSTRAINT fk_operator_data_source_id960ef40e;
ALTER TABLE ONLY public.renameoperator DROP CONSTRAINT fk_operator_data_source_id94027222;
ALTER TABLE ONLY public.reduceoperator DROP CONSTRAINT fk_operator_data_source_id5e11884a;
ALTER TABLE ONLY public.scanoperator DROP CONSTRAINT fk_operator_data_source_id3e49da61;
ALTER TABLE ONLY public.ontology_terms DROP CONSTRAINT fk_ontology_term_parent_ontology_term_id;
ALTER TABLE ONLY public.schematic_correspondences DROP CONSTRAINT fk_mmc_dataspace_idf771ce9b4971e6e7;
ALTER TABLE ONLY public.mappings DROP CONSTRAINT fk_mmc_dataspace_idf771ce9b3a3cf165;
ALTER TABLE ONLY public.one_to_one_matchings DROP CONSTRAINT fk_mmc_dataspace_idf771ce9b158d101d25461f52;
ALTER TABLE ONLY public.query_results DROP CONSTRAINT fk_mmc_dataspace_ideb2c673f;
ALTER TABLE ONLY public.setoperator DROP CONSTRAINT fk_mmc_dataspace_ide9fcaf72bc266f06;
ALTER TABLE ONLY public.typecastoperator DROP CONSTRAINT fk_mmc_dataspace_ide9fcaf72a776797d;
ALTER TABLE ONLY public.joinoperator DROP CONSTRAINT fk_mmc_dataspace_ide9fcaf72960ef40e;
ALTER TABLE ONLY public.renameoperator DROP CONSTRAINT fk_mmc_dataspace_ide9fcaf7294027222;
ALTER TABLE ONLY public.reduceoperator DROP CONSTRAINT fk_mmc_dataspace_ide9fcaf725e11884a;
ALTER TABLE ONLY public.scanoperator DROP CONSTRAINT fk_mmc_dataspace_ide9fcaf723e49da61;
ALTER TABLE ONLY public.schemas DROP CONSTRAINT fk_mmc_dataspace_id9d110ad2;
ALTER TABLE ONLY public.result_field_result_values DROP CONSTRAINT fk_mmc_dataspace_id72e875bd;
ALTER TABLE ONLY public.predicates DROP CONSTRAINT fk_mmc_dataspace_id6218799c;
ALTER TABLE ONLY public.result_instances DROP CONSTRAINT fk_mmc_dataspace_id582304bc;
ALTER TABLE ONLY public.super_lexicals DROP CONSTRAINT fk_mmc_dataspace_id558d94a2edb2b7f3;
ALTER TABLE ONLY public.temp_construct DROP CONSTRAINT fk_mmc_dataspace_id558d94a27232f10c;
ALTER TABLE ONLY public.super_relationships DROP CONSTRAINT fk_mmc_dataspace_id558d94a263923737;
ALTER TABLE ONLY public.super_abstracts DROP CONSTRAINT fk_mmc_dataspace_id558d94a23d29608d;
ALTER TABLE ONLY public.queries DROP CONSTRAINT fk_mmc_dataspace_id51d76346;
ALTER TABLE ONLY public.datasources DROP CONSTRAINT fk_mmc_dataspace_id4c4e2cae;
ALTER TABLE ONLY public.participation_of_cmc_in_super_relationship DROP CONSTRAINT fk_mmc_dataspace_id26f08d46;
ALTER TABLE ONLY public.mappings DROP CONSTRAINT fk_mapping_query2_id;
ALTER TABLE ONLY public.mappings DROP CONSTRAINT fk_mapping_query1_id;
ALTER TABLE ONLY public.setoperator DROP CONSTRAINT fk_mapping_operator_result_type_idbc266f06;
ALTER TABLE ONLY public.typecastoperator DROP CONSTRAINT fk_mapping_operator_result_type_ida776797d;
ALTER TABLE ONLY public.joinoperator DROP CONSTRAINT fk_mapping_operator_result_type_id960ef40e;
ALTER TABLE ONLY public.renameoperator DROP CONSTRAINT fk_mapping_operator_result_type_id94027222;
ALTER TABLE ONLY public.reduceoperator DROP CONSTRAINT fk_mapping_operator_result_type_id5e11884a;
ALTER TABLE ONLY public.scanoperator DROP CONSTRAINT fk_mapping_operator_result_type_id3e49da61;
ALTER TABLE ONLY public.super_lexicals DROP CONSTRAINT fk_canonical_model_construct_schema_idedb2b7f3;
ALTER TABLE ONLY public.temp_construct DROP CONSTRAINT fk_canonical_model_construct_schema_id7232f10c;
ALTER TABLE ONLY public.super_relationships DROP CONSTRAINT fk_canonical_model_construct_schema_id63923737;
ALTER TABLE ONLY public.super_abstracts DROP CONSTRAINT fk_canonical_model_construct_schema_id3d29608d;
ALTER TABLE ONLY public.annotations DROP CONSTRAINT fk_annotation_user_id;
ALTER TABLE ONLY public.annotations DROP CONSTRAINT fk_annotation_ontology_term_id;
ALTER TABLE ONLY public.dataspace_datasources DROP CONSTRAINT fk90a07c6bfee88a44;
ALTER TABLE ONLY public.dataspace_datasources DROP CONSTRAINT fk90a07c6ba5346195;
ALTER TABLE ONLY public.schematic_correspondences_utilised_for_mappings_provenance DROP CONSTRAINT fk757b51097eb1d99;
ALTER TABLE ONLY public.schematic_correspondences_utilised_for_mappings_provenance DROP CONSTRAINT fk757b51091820cbd9;
ALTER TABLE ONLY public.ontology_term_enum_values DROP CONSTRAINT fk660937bd79ff3e2e;
ALTER TABLE ONLY public.predicates DROP CONSTRAINT fk6218799cfd52399a;
ALTER TABLE ONLY public.predicates DROP CONSTRAINT fk6218799c19d4357a;
ALTER TABLE ONLY public.annotation_annotated_constructs DROP CONSTRAINT fk57a47cb345ade094;
ALTER TABLE ONLY public.schematic_correspondences_to_mapping_provenance DROP CONSTRAINT fk5371733ec94546c5;
ALTER TABLE ONLY public.result_instance_result_values DROP CONSTRAINT fk52a767bcdebf6810;
ALTER TABLE ONLY public.result_instance_result_values DROP CONSTRAINT fk52a767bc7f33a796;
ALTER TABLE ONLY public.query_super_abstracts DROP CONSTRAINT fk482186f6bc26ce98;
ALTER TABLE ONLY public.query_super_abstracts DROP CONSTRAINT fk482186f65914c355;
ALTER TABLE ONLY public.annotation_constraining_constructs DROP CONSTRAINT fk47c8342045ade094;
ALTER TABLE ONLY public.query_result_schema_of_data_source DROP CONSTRAINT fk3d200fdba8a2e650;
ALTER TABLE ONLY public.query_result_schema_of_data_source DROP CONSTRAINT fk3d200fdb8af976df;
ALTER TABLE ONLY public.parameter_applied_to_construct DROP CONSTRAINT fk35575e8b58e9243e;
ALTER TABLE ONLY public.result_type_result_fields DROP CONSTRAINT fk31aec838e80d60c4;
ALTER TABLE ONLY public.result_type_result_fields DROP CONSTRAINT fk31aec838398bd4f0;
ALTER TABLE ONLY public.dataspace_schemas DROP CONSTRAINT fk21d2c10fa5346195;
ALTER TABLE ONLY public.dataspace_schemas DROP CONSTRAINT fk21d2c10f880c25a4;
ALTER TABLE ONLY public.query_schemas DROP CONSTRAINT fk1c06aa3b880c25a4;
ALTER TABLE ONLY public.query_schemas DROP CONSTRAINT fk1c06aa3b5914c355;
ALTER TABLE ONLY public.users DROP CONSTRAINT users_user_name_key;
ALTER TABLE ONLY public.users DROP CONSTRAINT users_pkey;
ALTER TABLE ONLY public.users DROP CONSTRAINT users_email_key;
ALTER TABLE ONLY public.typecastoperator DROP CONSTRAINT typecastoperator_pkey;
ALTER TABLE ONLY public.temp_construct DROP CONSTRAINT temp_construct_pkey;
ALTER TABLE ONLY public.super_relationships DROP CONSTRAINT super_relationships_pkey;
ALTER TABLE ONLY public.super_lexicals DROP CONSTRAINT super_lexicals_pkey;
ALTER TABLE ONLY public.super_abstracts DROP CONSTRAINT super_abstracts_pkey;
ALTER TABLE ONLY public.setoperator DROP CONSTRAINT setoperator_pkey;
ALTER TABLE ONLY public.schematic_correspondences_utilised_for_mappings_provenance DROP CONSTRAINT schematic_correspondences_utilised_for_mappings_provenance_pkey;
ALTER TABLE ONLY public.schematic_correspondences_to_mapping_provenance DROP CONSTRAINT schematic_correspondences_to_mapping_provenance_pkey;
ALTER TABLE ONLY public.schematic_correspondences DROP CONSTRAINT schematic_correspondences_pkey;
ALTER TABLE ONLY public.schematic_correspondence_reconciling_expressions_2 DROP CONSTRAINT schematic_correspondence_reconciling_expressions_2_pkey;
ALTER TABLE ONLY public.schematic_correspondence_reconciling_expressions_1 DROP CONSTRAINT schematic_correspondence_reconciling_expressions_1_pkey;
ALTER TABLE ONLY public.schematic_correspondence_reconciling_expressions_1 DROP CONSTRAINT schematic_correspondence_reconcil_reconciling_expression_id_key;
ALTER TABLE ONLY public.schematic_correspondence_reconciling_expressions_2 DROP CONSTRAINT schematic_correspondence_reconci_reconciling_expression_id_key1;
ALTER TABLE ONLY public.schemas DROP CONSTRAINT schemas_pkey;
ALTER TABLE ONLY public.scanoperator DROP CONSTRAINT scanoperator_pkey;
ALTER TABLE ONLY public.sample_kde DROP CONSTRAINT sample_kde_pkey;
ALTER TABLE ONLY public.roles DROP CONSTRAINT roles_pkey;
ALTER TABLE ONLY public.role_users DROP CONSTRAINT role_users_pkey;
ALTER TABLE ONLY public.result_types DROP CONSTRAINT result_types_pkey;
ALTER TABLE ONLY public.result_type_result_fields DROP CONSTRAINT result_type_result_fields_result_field_id_key;
ALTER TABLE ONLY public.result_type_result_fields DROP CONSTRAINT result_type_result_fields_pkey;
ALTER TABLE ONLY public.result_instances DROP CONSTRAINT result_instances_pkey;
ALTER TABLE ONLY public.result_instance_result_values DROP CONSTRAINT result_instance_result_values_pkey;
ALTER TABLE ONLY public.result_instance_mapping DROP CONSTRAINT result_instance_mapping_pkey;
ALTER TABLE ONLY public.result_fields DROP CONSTRAINT result_fields_pkey;
ALTER TABLE ONLY public.result_field_result_values DROP CONSTRAINT result_field_result_values_pkey;
ALTER TABLE ONLY public.renameoperator DROP CONSTRAINT renameoperator_pkey;
ALTER TABLE ONLY public.reduceoperator DROP CONSTRAINT reduceoperator_pkey;
ALTER TABLE ONLY public.reconciling_expression DROP CONSTRAINT reconciling_expression_pkey;
ALTER TABLE ONLY public.query_super_abstracts DROP CONSTRAINT query_super_abstracts_pkey;
ALTER TABLE ONLY public.query_schemas DROP CONSTRAINT query_schemas_pkey;
ALTER TABLE ONLY public.query_results DROP CONSTRAINT query_results_pkey;
ALTER TABLE ONLY public.query_result_schema_of_data_source DROP CONSTRAINT query_result_schema_of_data_source_pkey;
ALTER TABLE ONLY public.query_result_mapping DROP CONSTRAINT query_result_mapping_pkey;
ALTER TABLE ONLY public.query_datasources DROP CONSTRAINT query_datasources_pkey;
ALTER TABLE ONLY public.queries DROP CONSTRAINT queries_pkey;
ALTER TABLE ONLY public.properties DROP CONSTRAINT properties_pkey;
ALTER TABLE ONLY public.predicates DROP CONSTRAINT predicates_pkey;
ALTER TABLE ONLY public.participation_specifying_super_lexical DROP CONSTRAINT participation_specifying_super_lexical_pkey;
ALTER TABLE ONLY public.participation_of_cmc_in_super_relationship DROP CONSTRAINT participation_of_cmc_in_super_relationship_pkey;
ALTER TABLE ONLY public.parameters DROP CONSTRAINT parameters_pkey;
ALTER TABLE ONLY public.parameter_applied_to_construct DROP CONSTRAINT parameter_applied_to_construct_pkey;
ALTER TABLE ONLY public.ontology_terms DROP CONSTRAINT ontology_terms_pkey;
ALTER TABLE ONLY public.one_to_one_matchings DROP CONSTRAINT one_to_one_matchings_pkey;
ALTER TABLE ONLY public.morphism_constructs2 DROP CONSTRAINT morphism_constructs2_pkey;
ALTER TABLE ONLY public.morphism_constructs1 DROP CONSTRAINT morphism_constructs1_pkey;
ALTER TABLE ONLY public.mappings DROP CONSTRAINT mappings_pkey;
ALTER TABLE ONLY public.kernel_density_estimators DROP CONSTRAINT kernel_density_estimators_pkey;
ALTER TABLE ONLY public.joinoperator DROP CONSTRAINT joinoperator_pkey;
ALTER TABLE ONLY public.dataspaces DROP CONSTRAINT dataspaces_pkey;
ALTER TABLE ONLY public.dataspace_users DROP CONSTRAINT dataspace_users_pkey;
ALTER TABLE ONLY public.dataspace_schemas DROP CONSTRAINT dataspace_schemas_pkey;
ALTER TABLE ONLY public.dataspace_datasources DROP CONSTRAINT dataspace_datasources_pkey;
ALTER TABLE ONLY public.datasources DROP CONSTRAINT datasources_pkey;
ALTER TABLE ONLY public.constructs_morphisms DROP CONSTRAINT constructs_morphisms_pkey;
ALTER TABLE ONLY public.annotations DROP CONSTRAINT annotations_pkey;
ALTER TABLE ONLY public.annotation_constraining_constructs DROP CONSTRAINT annotation_constraining_constructs_pkey;
ALTER TABLE ONLY public.annotation_annotated_constructs DROP CONSTRAINT annotation_annotated_constructs_pkey;
DROP TABLE public.users;
DROP TABLE public.typecastoperator;
DROP TABLE public.temp_construct;
DROP TABLE public.super_relationships;
DROP TABLE public.super_lexicals;
DROP TABLE public.super_abstracts;
DROP TABLE public.setoperator;
DROP TABLE public.schematic_correspondences_utilised_for_mappings_provenance;
DROP TABLE public.schematic_correspondences_to_mapping_provenance;
DROP TABLE public.schematic_correspondences;
DROP TABLE public.schematic_correspondence_reconciling_expressions_2;
DROP TABLE public.schematic_correspondence_reconciling_expressions_1;
DROP TABLE public.schemas;
DROP TABLE public.scanoperator;
DROP TABLE public.sample_kde;
DROP TABLE public.roles;
DROP TABLE public.role_users;
DROP TABLE public.result_types;
DROP TABLE public.result_type_result_fields;
DROP TABLE public.result_instances;
DROP TABLE public.result_instance_result_values;
DROP TABLE public.result_instance_mapping;
DROP TABLE public.result_fields;
DROP TABLE public.result_field_result_values;
DROP TABLE public.renameoperator;
DROP TABLE public.reduceoperator;
DROP TABLE public.reconciling_expression;
DROP TABLE public.query_super_abstracts;
DROP TABLE public.query_schemas;
DROP TABLE public.query_results;
DROP TABLE public.query_result_schema_of_data_source;
DROP TABLE public.query_result_result_instance;
DROP TABLE public.query_result_mapping;
DROP TABLE public.query_datasources;
DROP TABLE public.queries;
DROP TABLE public.properties;
DROP TABLE public.predicates;
DROP TABLE public.participation_specifying_super_lexical;
DROP TABLE public.participation_of_cmc_in_super_relationship;
DROP TABLE public.parameters;
DROP TABLE public.parameter_applied_to_construct;
DROP TABLE public.ontology_terms;
DROP TABLE public.ontology_term_enum_values;
DROP TABLE public.one_to_one_matchings;
DROP TABLE public.morphism_constructs2;
DROP TABLE public.morphism_constructs1;
DROP TABLE public.mappings;
DROP TABLE public.kernel_density_estimators;
DROP TABLE public.joinoperator;
DROP SEQUENCE public.hibernate_sequence;
DROP TABLE public.dataspaces;
DROP TABLE public.dataspace_users;
DROP TABLE public.dataspace_schemas;
DROP TABLE public.dataspace_datasources;
DROP TABLE public.datasources;
DROP TABLE public.constructs_morphisms;
DROP TABLE public.annotations;
DROP TABLE public.annotation_constraining_constructs;
DROP TABLE public.annotation_annotated_constructs;
DROP EXTENSION plpgsql;
DROP SCHEMA public;
--
-- Name: public; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA public;


ALTER SCHEMA public OWNER TO postgres;

--
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: postgres
--

COMMENT ON SCHEMA public IS 'standard public schema';


--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: annotation_annotated_constructs; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE annotation_annotated_constructs (
    annotation_id bigint NOT NULL,
    annotated_construct_id bigint NOT NULL
);


ALTER TABLE public.annotation_annotated_constructs OWNER TO postgres;

--
-- Name: annotation_constraining_constructs; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE annotation_constraining_constructs (
    annotation_id bigint NOT NULL,
    constraining_construct_id bigint NOT NULL
);


ALTER TABLE public.annotation_constraining_constructs OWNER TO postgres;

--
-- Name: annotations; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE annotations (
    id bigint NOT NULL,
    obj_version integer,
    "timestamp" time without time zone,
    annotation_value character varying(255) NOT NULL,
    annotation_ontology_term_id bigint,
    annotation_user_id bigint
);


ALTER TABLE public.annotations OWNER TO postgres;

--
-- Name: constructs_morphisms; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE constructs_morphisms (
    construct_id bigint NOT NULL,
    morphism_id bigint NOT NULL
);


ALTER TABLE public.constructs_morphisms OWNER TO postgres;

--
-- Name: datasources; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE datasources (
    id bigint NOT NULL,
    obj_version integer,
    construct_is_user_specified boolean,
    mmc_dataspace_id bigint,
    connection_url character varying(255) NOT NULL,
    description character varying(255),
    driver_class character varying(255),
    is_rdf character varying(255),
    data_source_name character varying(255),
    password character varying(255),
    schema_url character varying(255),
    sparql_url character varying(255),
    user_name character varying(255)
);


ALTER TABLE public.datasources OWNER TO postgres;

--
-- Name: dataspace_datasources; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE dataspace_datasources (
    dataspace_id bigint NOT NULL,
    datasource_id bigint NOT NULL
);


ALTER TABLE public.dataspace_datasources OWNER TO postgres;

--
-- Name: dataspace_schemas; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE dataspace_schemas (
    dataspace_id bigint NOT NULL,
    schema_id bigint NOT NULL
);


ALTER TABLE public.dataspace_schemas OWNER TO postgres;

--
-- Name: dataspace_users; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE dataspace_users (
    dataspace_id bigint NOT NULL,
    user_id bigint NOT NULL
);


ALTER TABLE public.dataspace_users OWNER TO postgres;

--
-- Name: dataspaces; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE dataspaces (
    id bigint NOT NULL,
    obj_version integer,
    dataspace_name character varying(255)
);


ALTER TABLE public.dataspaces OWNER TO postgres;

--
-- Name: hibernate_sequence; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.hibernate_sequence OWNER TO postgres;

--
-- Name: joinoperator; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE joinoperator (
    id bigint NOT NULL,
    obj_version integer,
    construct_is_user_specified boolean,
    mmc_dataspace_id bigint,
    mapping_operator_and_or character varying(255),
    mapping_operator_variable_name character varying(255),
    mapping_operator_data_source_id bigint,
    mapping_operator_lhs_input_operator_id bigint,
    mapping_operator_mapping_id bigint,
    reconcilingexpression_id bigint,
    mapping_operator_result_type_id bigint,
    mapping_operator_rhs_input_operator_id bigint
);


ALTER TABLE public.joinoperator OWNER TO postgres;

--
-- Name: kernel_density_estimators; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE kernel_density_estimators (
    id bigint NOT NULL,
    obj_version integer,
    estimator_name character varying(255) NOT NULL,
    kernel_smoothing double precision,
    estimator_is_bounded boolean,
    case_type character varying(255),
    estimator_type character varying(255),
    kernel_type character varying(255)
);


ALTER TABLE public.kernel_density_estimators OWNER TO postgres;

--
-- Name: mappings; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE mappings (
    id bigint NOT NULL,
    obj_version integer,
    construct_is_user_specified boolean,
    mmc_dataspace_id bigint,
    cardinality_type character varying(255),
    mapping_query1_string character varying(1000),
    mapping_query2_string character varying(1000),
    mapping_query1_id bigint,
    mapping_query2_id bigint
);


ALTER TABLE public.mappings OWNER TO postgres;

--
-- Name: morphism_constructs1; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE morphism_constructs1 (
    morphism_id bigint NOT NULL,
    construct1_id bigint NOT NULL
);


ALTER TABLE public.morphism_constructs1 OWNER TO postgres;

--
-- Name: morphism_constructs2; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE morphism_constructs2 (
    morphism_id bigint NOT NULL,
    construct2_id bigint NOT NULL
);


ALTER TABLE public.morphism_constructs2 OWNER TO postgres;

--
-- Name: one_to_one_matchings; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE one_to_one_matchings (
    id bigint NOT NULL,
    obj_version integer,
    construct_is_user_specified boolean,
    mmc_dataspace_id bigint,
    cardinality_type character varying(255),
    abs_error double precision,
    class_label character varying(255),
    matcher_name character varying(255),
    matching_score double precision,
    squared_error double precision,
    parent_match_id bigint,
    matching_construct1_id bigint,
    matching_construct2_id bigint
);


ALTER TABLE public.one_to_one_matchings OWNER TO postgres;

--
-- Name: ontology_term_enum_values; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE ontology_term_enum_values (
    enum_id bigint NOT NULL,
    enum_value character varying(255)
);


ALTER TABLE public.ontology_term_enum_values OWNER TO postgres;

--
-- Name: ontology_terms; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE ontology_terms (
    id bigint NOT NULL,
    obj_version integer,
    ontology_term_data_type character varying(255),
    ontology_term_name character varying(255) NOT NULL,
    parent_ontology_term_id bigint
);


ALTER TABLE public.ontology_terms OWNER TO postgres;

--
-- Name: parameter_applied_to_construct; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE parameter_applied_to_construct (
    parameter_id bigint NOT NULL,
    construct_id bigint NOT NULL
);


ALTER TABLE public.parameter_applied_to_construct OWNER TO postgres;

--
-- Name: parameters; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE parameters (
    id bigint NOT NULL,
    obj_version integer,
    parameter_direction character varying(255) NOT NULL,
    parameter_name character varying(255) NOT NULL,
    parameter_value character varying(255) NOT NULL,
    schematic_correspondence_id bigint NOT NULL
);


ALTER TABLE public.parameters OWNER TO postgres;

--
-- Name: participation_of_cmc_in_super_relationship; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE participation_of_cmc_in_super_relationship (
    id bigint NOT NULL,
    obj_version integer,
    construct_is_user_specified boolean,
    mmc_dataspace_id bigint,
    role_of_construct_in_super_relationship character varying(255),
    construct_id bigint,
    super_relationship_id bigint
);


ALTER TABLE public.participation_of_cmc_in_super_relationship OWNER TO postgres;

--
-- Name: participation_specifying_super_lexical; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE participation_specifying_super_lexical (
    super_lexical_id bigint NOT NULL,
    participation_in_super_relationship_id bigint NOT NULL
);


ALTER TABLE public.participation_specifying_super_lexical OWNER TO postgres;

--
-- Name: predicates; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE predicates (
    id bigint NOT NULL,
    obj_version integer,
    construct_is_user_specified boolean,
    mmc_dataspace_id bigint,
    andor character varying(255),
    literal1 character varying(255),
    literal2 character varying(255),
    operator character varying(255),
    predicate_super_lexical1_id bigint,
    predicate_super_lexical2_id bigint,
    scan_predicate_id bigint,
    join_predicate_id bigint
);


ALTER TABLE public.predicates OWNER TO postgres;

--
-- Name: properties; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE properties (
    id bigint NOT NULL,
    obj_version integer,
    property_data_type character varying(255),
    property_lang character varying(255),
    property_name character varying(255) NOT NULL,
    property_value character varying(255) NOT NULL,
    property_id bigint NOT NULL
);


ALTER TABLE public.properties OWNER TO postgres;

--
-- Name: queries; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE queries (
    id bigint NOT NULL,
    obj_version integer,
    construct_is_user_specified boolean,
    mmc_dataspace_id bigint,
    query_description character varying(255),
    query_name character varying(255),
    query_string character varying(1000),
    query_dataspace_id bigint,
    query_root_operator_id bigint,
    query_user_id bigint
);


ALTER TABLE public.queries OWNER TO postgres;

--
-- Name: query_datasources; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE query_datasources (
    query_id bigint NOT NULL,
    datasource_id bigint NOT NULL
);


ALTER TABLE public.query_datasources OWNER TO postgres;

--
-- Name: query_result_mapping; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE query_result_mapping (
    query_result_id bigint NOT NULL,
    mapping_id bigint NOT NULL
);


ALTER TABLE public.query_result_mapping OWNER TO postgres;

--
-- Name: query_result_result_instance; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE query_result_result_instance (
    query_result_id bigint NOT NULL,
    result_instance_id bigint NOT NULL
);


ALTER TABLE public.query_result_result_instance OWNER TO postgres;

--
-- Name: query_result_schema_of_data_source; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE query_result_schema_of_data_source (
    query_result_id bigint NOT NULL,
    schema_of_data_source_id bigint NOT NULL
);


ALTER TABLE public.query_result_schema_of_data_source OWNER TO postgres;

--
-- Name: query_results; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE query_results (
    id bigint NOT NULL,
    obj_version integer,
    construct_is_user_specified boolean,
    mmc_dataspace_id bigint,
    query_result_dataspace_id bigint,
    query_result_query_id bigint,
    query_result_result_type_id bigint
);


ALTER TABLE public.query_results OWNER TO postgres;

--
-- Name: query_schemas; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE query_schemas (
    query_id bigint NOT NULL,
    schema_id bigint NOT NULL
);


ALTER TABLE public.query_schemas OWNER TO postgres;

--
-- Name: query_super_abstracts; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE query_super_abstracts (
    query_id bigint NOT NULL,
    super_abstract_id bigint NOT NULL
);


ALTER TABLE public.query_super_abstracts OWNER TO postgres;

--
-- Name: reconciling_expression; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE reconciling_expression (
    id bigint NOT NULL,
    obj_version integer,
    reconciling_expression_expression character varying(10000),
    reconciling_expression_type character varying(255),
    reconciling_expression_applied_to_canonical_model_construct bigint,
    reconciling_expression_join_pred_1 bigint,
    reconciling_expression_join_pred_2 bigint,
    reconciling_expression_selection_target_super_abstract bigint
);


ALTER TABLE public.reconciling_expression OWNER TO postgres;

--
-- Name: reduceoperator; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE reduceoperator (
    id bigint NOT NULL,
    obj_version integer,
    construct_is_user_specified boolean,
    mmc_dataspace_id bigint,
    mapping_operator_and_or character varying(255),
    mapping_operator_variable_name character varying(255),
    mapping_operator_data_source_id bigint,
    mapping_operator_lhs_input_operator_id bigint,
    mapping_operator_mapping_id bigint,
    reconcilingexpression_id bigint,
    mapping_operator_result_type_id bigint,
    mapping_operator_rhs_input_operator_id bigint
);


ALTER TABLE public.reduceoperator OWNER TO postgres;

--
-- Name: renameoperator; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE renameoperator (
    id bigint NOT NULL,
    obj_version integer,
    construct_is_user_specified boolean,
    mmc_dataspace_id bigint,
    mapping_operator_and_or character varying(255),
    mapping_operator_variable_name character varying(255),
    mapping_operator_data_source_id bigint,
    mapping_operator_lhs_input_operator_id bigint,
    mapping_operator_mapping_id bigint,
    reconcilingexpression_id bigint,
    mapping_operator_result_type_id bigint,
    mapping_operator_rhs_input_operator_id bigint,
    rename_new_name character varying(255),
    rename_canonical_model_construct_id bigint
);


ALTER TABLE public.renameoperator OWNER TO postgres;

--
-- Name: result_field_result_values; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE result_field_result_values (
    id bigint NOT NULL,
    obj_version integer,
    construct_is_user_specified boolean,
    mmc_dataspace_id bigint,
    result_field_name character varying(10000),
    result_value_value character varying(50000)
);


ALTER TABLE public.result_field_result_values OWNER TO postgres;

--
-- Name: result_fields; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE result_fields (
    id bigint NOT NULL,
    obj_version integer,
    result_field_name character varying(255),
    result_field_type character varying(255),
    result_field_index integer,
    construct_id bigint
);


ALTER TABLE public.result_fields OWNER TO postgres;

--
-- Name: result_instance_mapping; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE result_instance_mapping (
    result_instance_id bigint NOT NULL,
    mapping_id bigint NOT NULL
);


ALTER TABLE public.result_instance_mapping OWNER TO postgres;

--
-- Name: result_instance_result_values; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE result_instance_result_values (
    result_instance_id bigint NOT NULL,
    result_field_result_values_id bigint NOT NULL
);


ALTER TABLE public.result_instance_result_values OWNER TO postgres;

--
-- Name: result_instances; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE result_instances (
    id bigint NOT NULL,
    obj_version integer,
    construct_is_user_specified boolean,
    mmc_dataspace_id bigint,
    result_instance_query_id bigint,
    result_type_id bigint
);


ALTER TABLE public.result_instances OWNER TO postgres;

--
-- Name: result_type_result_fields; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE result_type_result_fields (
    result_type_id bigint NOT NULL,
    result_field_id bigint NOT NULL
);


ALTER TABLE public.result_type_result_fields OWNER TO postgres;

--
-- Name: result_types; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE result_types (
    id bigint NOT NULL,
    obj_version integer
);


ALTER TABLE public.result_types OWNER TO postgres;

--
-- Name: role_users; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE role_users (
    role_id bigint NOT NULL,
    user_id bigint NOT NULL
);


ALTER TABLE public.role_users OWNER TO postgres;

--
-- Name: roles; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE roles (
    id bigint NOT NULL,
    obj_version integer,
    role_role_type character varying(255)
);


ALTER TABLE public.roles OWNER TO postgres;

--
-- Name: sample_kde; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE sample_kde (
    id bigint NOT NULL,
    obj_version integer,
    sample_value double precision NOT NULL,
    estimator_id bigint NOT NULL
);


ALTER TABLE public.sample_kde OWNER TO postgres;

--
-- Name: scanoperator; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE scanoperator (
    id bigint NOT NULL,
    obj_version integer,
    construct_is_user_specified boolean,
    mmc_dataspace_id bigint,
    mapping_operator_and_or character varying(255),
    mapping_operator_variable_name character varying(255),
    mapping_operator_data_source_id bigint,
    mapping_operator_lhs_input_operator_id bigint,
    mapping_operator_mapping_id bigint,
    reconcilingexpression_id bigint,
    mapping_operator_result_type_id bigint,
    mapping_operator_rhs_input_operator_id bigint,
    scan_super_abstract_id bigint
);


ALTER TABLE public.scanoperator OWNER TO postgres;

--
-- Name: schemas; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE schemas (
    id bigint NOT NULL,
    obj_version integer,
    construct_is_user_specified boolean,
    mmc_dataspace_id bigint,
    model_type character varying(255),
    schema_name character varying(255) NOT NULL,
    data_source_id bigint
);


ALTER TABLE public.schemas OWNER TO postgres;

--
-- Name: schematic_correspondence_reconciling_expressions_1; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE schematic_correspondence_reconciling_expressions_1 (
    schematic_correspondence_id bigint NOT NULL,
    reconciling_expression_id bigint NOT NULL
);


ALTER TABLE public.schematic_correspondence_reconciling_expressions_1 OWNER TO postgres;

--
-- Name: schematic_correspondence_reconciling_expressions_2; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE schematic_correspondence_reconciling_expressions_2 (
    schematic_correspondence_id bigint NOT NULL,
    reconciling_expression_id bigint NOT NULL
);


ALTER TABLE public.schematic_correspondence_reconciling_expressions_2 OWNER TO postgres;

--
-- Name: schematic_correspondences; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE schematic_correspondences (
    id bigint NOT NULL,
    obj_version integer,
    construct_is_user_specified boolean,
    mmc_dataspace_id bigint,
    cardinality_type character varying(255),
    construct_related_schematic_correspondence_type character varying(255),
    schematic_correspondence_description character varying(255),
    parameter_direction character varying(255) NOT NULL,
    schematic_correspondence_name character varying(255),
    schematic_correspondence_type character varying(255),
    schematic_correspondence_short_name character varying(255),
    parent_schematic_correspondence_id bigint
);


ALTER TABLE public.schematic_correspondences OWNER TO postgres;

--
-- Name: schematic_correspondences_to_mapping_provenance; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE schematic_correspondences_to_mapping_provenance (
    id bigint NOT NULL,
    obj_version integer,
    mapping_id bigint
);


ALTER TABLE public.schematic_correspondences_to_mapping_provenance OWNER TO postgres;

--
-- Name: schematic_correspondences_utilised_for_mappings_provenance; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE schematic_correspondences_utilised_for_mappings_provenance (
    provenance_id bigint NOT NULL,
    schematic_correspondence_id bigint NOT NULL
);


ALTER TABLE public.schematic_correspondences_utilised_for_mappings_provenance OWNER TO postgres;

--
-- Name: setoperator; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE setoperator (
    id bigint NOT NULL,
    obj_version integer,
    construct_is_user_specified boolean,
    mmc_dataspace_id bigint,
    mapping_operator_and_or character varying(255),
    mapping_operator_variable_name character varying(255),
    mapping_operator_data_source_id bigint,
    mapping_operator_lhs_input_operator_id bigint,
    mapping_operator_mapping_id bigint,
    reconcilingexpression_id bigint,
    mapping_operator_result_type_id bigint,
    mapping_operator_rhs_input_operator_id bigint,
    setop_type character varying(255)
);


ALTER TABLE public.setoperator OWNER TO postgres;

--
-- Name: super_abstracts; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE super_abstracts (
    id bigint NOT NULL,
    obj_version integer,
    construct_is_user_specified boolean,
    mmc_dataspace_id bigint,
    canonical_model_construct_is_global boolean,
    canonical_model_construct_is_virtual boolean,
    canonical_model_construct_name character varying(255) NOT NULL,
    canonical_model_construct_type character varying(255),
    canonical_model_construct_schema_id bigint,
    super_abstract_cardinality integer,
    super_abstract_midst_super_model_type character varying(255),
    super_abstract_model_specific_type character varying(255),
    parent_super_abstract_id bigint
);


ALTER TABLE public.super_abstracts OWNER TO postgres;

--
-- Name: super_lexicals; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE super_lexicals (
    id bigint NOT NULL,
    obj_version integer,
    construct_is_user_specified boolean,
    mmc_dataspace_id bigint,
    canonical_model_construct_is_global boolean,
    canonical_model_construct_is_virtual boolean,
    canonical_model_construct_name character varying(255) NOT NULL,
    canonical_model_construct_type character varying(255),
    canonical_model_construct_schema_id bigint,
    super_lexical_data_type character varying(255),
    super_lexical_is_identifier boolean,
    super_lexical_is_nullable boolean,
    super_lexical_is_optional boolean,
    super_lexical_max_value_size integer,
    super_lexical_midst_super_model_type character varying(255),
    super_lexical_model_specific_type character varying(255),
    super_lexical_number_of_distinct_values integer,
    super_lexical_parent_super_abstract_id bigint,
    parent_super_lexical_id bigint,
    parent_super_relationship_id bigint,
    super_lexical_id bigint,
    mapkey character varying(255)
);


ALTER TABLE public.super_lexicals OWNER TO postgres;

--
-- Name: super_relationships; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE super_relationships (
    id bigint NOT NULL,
    obj_version integer,
    construct_is_user_specified boolean,
    mmc_dataspace_id bigint,
    canonical_model_construct_is_global boolean,
    canonical_model_construct_is_virtual boolean,
    canonical_model_construct_name character varying(255) NOT NULL,
    canonical_model_construct_type character varying(255),
    canonical_model_construct_schema_id bigint,
    super_relationship_midst_super_model_type character varying(255),
    super_relationship_model_specific_type character varying(255),
    super_relationship_type character varying(255),
    super_relationship_generalised_super_abstract_id bigint
);


ALTER TABLE public.super_relationships OWNER TO postgres;

--
-- Name: temp_construct; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE temp_construct (
    id bigint NOT NULL,
    obj_version integer,
    construct_is_user_specified boolean,
    mmc_dataspace_id bigint,
    canonical_model_construct_is_global boolean,
    canonical_model_construct_is_virtual boolean,
    canonical_model_construct_name character varying(255) NOT NULL,
    canonical_model_construct_type character varying(255),
    canonical_model_construct_schema_id bigint
);


ALTER TABLE public.temp_construct OWNER TO postgres;

--
-- Name: typecastoperator; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE typecastoperator (
    id bigint NOT NULL,
    obj_version integer,
    construct_is_user_specified boolean,
    mmc_dataspace_id bigint,
    mapping_operator_and_or character varying(255),
    mapping_operator_variable_name character varying(255),
    mapping_operator_data_source_id bigint,
    mapping_operator_lhs_input_operator_id bigint,
    mapping_operator_mapping_id bigint,
    reconcilingexpression_id bigint,
    mapping_operator_result_type_id bigint,
    mapping_operator_rhs_input_operator_id bigint,
    type_cast_new_type character varying(255),
    type_cast_canonical_model_construct_id bigint
);


ALTER TABLE public.typecastoperator OWNER TO postgres;

--
-- Name: users; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE users (
    id bigint NOT NULL,
    obj_version integer,
    accept_terms boolean,
    date_created timestamp without time zone,
    email character varying(255) NOT NULL,
    first_name character varying(255),
    institution_name character varying(255),
    last_name character varying(255),
    password character varying(255) NOT NULL,
    user_name character varying(255) NOT NULL
);


ALTER TABLE public.users OWNER TO postgres;

--
-- Data for Name: annotation_annotated_constructs; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY annotation_annotated_constructs (annotation_id, annotated_construct_id) FROM stdin;
\.
COPY annotation_annotated_constructs (annotation_id, annotated_construct_id) FROM '$$PATH$$/2343.dat';

--
-- Data for Name: annotation_constraining_constructs; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY annotation_constraining_constructs (annotation_id, constraining_construct_id) FROM stdin;
\.
COPY annotation_constraining_constructs (annotation_id, constraining_construct_id) FROM '$$PATH$$/2344.dat';

--
-- Data for Name: annotations; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY annotations (id, obj_version, "timestamp", annotation_value, annotation_ontology_term_id, annotation_user_id) FROM stdin;
\.
COPY annotations (id, obj_version, "timestamp", annotation_value, annotation_ontology_term_id, annotation_user_id) FROM '$$PATH$$/2342.dat';

--
-- Data for Name: constructs_morphisms; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY constructs_morphisms (construct_id, morphism_id) FROM stdin;
\.
COPY constructs_morphisms (construct_id, morphism_id) FROM '$$PATH$$/2345.dat';

--
-- Data for Name: datasources; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY datasources (id, obj_version, construct_is_user_specified, mmc_dataspace_id, connection_url, description, driver_class, is_rdf, data_source_name, password, schema_url, sparql_url, user_name) FROM stdin;
\.
COPY datasources (id, obj_version, construct_is_user_specified, mmc_dataspace_id, connection_url, description, driver_class, is_rdf, data_source_name, password, schema_url, sparql_url, user_name) FROM '$$PATH$$/2346.dat';

--
-- Data for Name: dataspace_datasources; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY dataspace_datasources (dataspace_id, datasource_id) FROM stdin;
\.
COPY dataspace_datasources (dataspace_id, datasource_id) FROM '$$PATH$$/2348.dat';

--
-- Data for Name: dataspace_schemas; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY dataspace_schemas (dataspace_id, schema_id) FROM stdin;
\.
COPY dataspace_schemas (dataspace_id, schema_id) FROM '$$PATH$$/2349.dat';

--
-- Data for Name: dataspace_users; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY dataspace_users (dataspace_id, user_id) FROM stdin;
\.
COPY dataspace_users (dataspace_id, user_id) FROM '$$PATH$$/2350.dat';

--
-- Data for Name: dataspaces; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY dataspaces (id, obj_version, dataspace_name) FROM stdin;
\.
COPY dataspaces (id, obj_version, dataspace_name) FROM '$$PATH$$/2347.dat';

--
-- Name: hibernate_sequence; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('hibernate_sequence', 868, true);


--
-- Data for Name: joinoperator; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY joinoperator (id, obj_version, construct_is_user_specified, mmc_dataspace_id, mapping_operator_and_or, mapping_operator_variable_name, mapping_operator_data_source_id, mapping_operator_lhs_input_operator_id, mapping_operator_mapping_id, reconcilingexpression_id, mapping_operator_result_type_id, mapping_operator_rhs_input_operator_id) FROM stdin;
\.
COPY joinoperator (id, obj_version, construct_is_user_specified, mmc_dataspace_id, mapping_operator_and_or, mapping_operator_variable_name, mapping_operator_data_source_id, mapping_operator_lhs_input_operator_id, mapping_operator_mapping_id, reconcilingexpression_id, mapping_operator_result_type_id, mapping_operator_rhs_input_operator_id) FROM '$$PATH$$/2351.dat';

--
-- Data for Name: kernel_density_estimators; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY kernel_density_estimators (id, obj_version, estimator_name, kernel_smoothing, estimator_is_bounded, case_type, estimator_type, kernel_type) FROM stdin;
\.
COPY kernel_density_estimators (id, obj_version, estimator_name, kernel_smoothing, estimator_is_bounded, case_type, estimator_type, kernel_type) FROM '$$PATH$$/2352.dat';

--
-- Data for Name: mappings; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY mappings (id, obj_version, construct_is_user_specified, mmc_dataspace_id, cardinality_type, mapping_query1_string, mapping_query2_string, mapping_query1_id, mapping_query2_id) FROM stdin;
\.
COPY mappings (id, obj_version, construct_is_user_specified, mmc_dataspace_id, cardinality_type, mapping_query1_string, mapping_query2_string, mapping_query1_id, mapping_query2_id) FROM '$$PATH$$/2353.dat';

--
-- Data for Name: morphism_constructs1; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY morphism_constructs1 (morphism_id, construct1_id) FROM stdin;
\.
COPY morphism_constructs1 (morphism_id, construct1_id) FROM '$$PATH$$/2354.dat';

--
-- Data for Name: morphism_constructs2; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY morphism_constructs2 (morphism_id, construct2_id) FROM stdin;
\.
COPY morphism_constructs2 (morphism_id, construct2_id) FROM '$$PATH$$/2355.dat';

--
-- Data for Name: one_to_one_matchings; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY one_to_one_matchings (id, obj_version, construct_is_user_specified, mmc_dataspace_id, cardinality_type, abs_error, class_label, matcher_name, matching_score, squared_error, parent_match_id, matching_construct1_id, matching_construct2_id) FROM stdin;
\.
COPY one_to_one_matchings (id, obj_version, construct_is_user_specified, mmc_dataspace_id, cardinality_type, abs_error, class_label, matcher_name, matching_score, squared_error, parent_match_id, matching_construct1_id, matching_construct2_id) FROM '$$PATH$$/2356.dat';

--
-- Data for Name: ontology_term_enum_values; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY ontology_term_enum_values (enum_id, enum_value) FROM stdin;
\.
COPY ontology_term_enum_values (enum_id, enum_value) FROM '$$PATH$$/2358.dat';

--
-- Data for Name: ontology_terms; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY ontology_terms (id, obj_version, ontology_term_data_type, ontology_term_name, parent_ontology_term_id) FROM stdin;
\.
COPY ontology_terms (id, obj_version, ontology_term_data_type, ontology_term_name, parent_ontology_term_id) FROM '$$PATH$$/2357.dat';

--
-- Data for Name: parameter_applied_to_construct; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY parameter_applied_to_construct (parameter_id, construct_id) FROM stdin;
\.
COPY parameter_applied_to_construct (parameter_id, construct_id) FROM '$$PATH$$/2360.dat';

--
-- Data for Name: parameters; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY parameters (id, obj_version, parameter_direction, parameter_name, parameter_value, schematic_correspondence_id) FROM stdin;
\.
COPY parameters (id, obj_version, parameter_direction, parameter_name, parameter_value, schematic_correspondence_id) FROM '$$PATH$$/2359.dat';

--
-- Data for Name: participation_of_cmc_in_super_relationship; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY participation_of_cmc_in_super_relationship (id, obj_version, construct_is_user_specified, mmc_dataspace_id, role_of_construct_in_super_relationship, construct_id, super_relationship_id) FROM stdin;
\.
COPY participation_of_cmc_in_super_relationship (id, obj_version, construct_is_user_specified, mmc_dataspace_id, role_of_construct_in_super_relationship, construct_id, super_relationship_id) FROM '$$PATH$$/2361.dat';

--
-- Data for Name: participation_specifying_super_lexical; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY participation_specifying_super_lexical (super_lexical_id, participation_in_super_relationship_id) FROM stdin;
\.
COPY participation_specifying_super_lexical (super_lexical_id, participation_in_super_relationship_id) FROM '$$PATH$$/2362.dat';

--
-- Data for Name: predicates; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY predicates (id, obj_version, construct_is_user_specified, mmc_dataspace_id, andor, literal1, literal2, operator, predicate_super_lexical1_id, predicate_super_lexical2_id, scan_predicate_id, join_predicate_id) FROM stdin;
\.
COPY predicates (id, obj_version, construct_is_user_specified, mmc_dataspace_id, andor, literal1, literal2, operator, predicate_super_lexical1_id, predicate_super_lexical2_id, scan_predicate_id, join_predicate_id) FROM '$$PATH$$/2363.dat';

--
-- Data for Name: properties; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY properties (id, obj_version, property_data_type, property_lang, property_name, property_value, property_id) FROM stdin;
\.
COPY properties (id, obj_version, property_data_type, property_lang, property_name, property_value, property_id) FROM '$$PATH$$/2364.dat';

--
-- Data for Name: queries; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY queries (id, obj_version, construct_is_user_specified, mmc_dataspace_id, query_description, query_name, query_string, query_dataspace_id, query_root_operator_id, query_user_id) FROM stdin;
\.
COPY queries (id, obj_version, construct_is_user_specified, mmc_dataspace_id, query_description, query_name, query_string, query_dataspace_id, query_root_operator_id, query_user_id) FROM '$$PATH$$/2365.dat';

--
-- Data for Name: query_datasources; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY query_datasources (query_id, datasource_id) FROM stdin;
\.
COPY query_datasources (query_id, datasource_id) FROM '$$PATH$$/2366.dat';

--
-- Data for Name: query_result_mapping; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY query_result_mapping (query_result_id, mapping_id) FROM stdin;
\.
COPY query_result_mapping (query_result_id, mapping_id) FROM '$$PATH$$/2368.dat';

--
-- Data for Name: query_result_result_instance; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY query_result_result_instance (query_result_id, result_instance_id) FROM stdin;
\.
COPY query_result_result_instance (query_result_id, result_instance_id) FROM '$$PATH$$/2369.dat';

--
-- Data for Name: query_result_schema_of_data_source; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY query_result_schema_of_data_source (query_result_id, schema_of_data_source_id) FROM stdin;
\.
COPY query_result_schema_of_data_source (query_result_id, schema_of_data_source_id) FROM '$$PATH$$/2370.dat';

--
-- Data for Name: query_results; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY query_results (id, obj_version, construct_is_user_specified, mmc_dataspace_id, query_result_dataspace_id, query_result_query_id, query_result_result_type_id) FROM stdin;
\.
COPY query_results (id, obj_version, construct_is_user_specified, mmc_dataspace_id, query_result_dataspace_id, query_result_query_id, query_result_result_type_id) FROM '$$PATH$$/2367.dat';

--
-- Data for Name: query_schemas; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY query_schemas (query_id, schema_id) FROM stdin;
\.
COPY query_schemas (query_id, schema_id) FROM '$$PATH$$/2371.dat';

--
-- Data for Name: query_super_abstracts; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY query_super_abstracts (query_id, super_abstract_id) FROM stdin;
\.
COPY query_super_abstracts (query_id, super_abstract_id) FROM '$$PATH$$/2372.dat';

--
-- Data for Name: reconciling_expression; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY reconciling_expression (id, obj_version, reconciling_expression_expression, reconciling_expression_type, reconciling_expression_applied_to_canonical_model_construct, reconciling_expression_join_pred_1, reconciling_expression_join_pred_2, reconciling_expression_selection_target_super_abstract) FROM stdin;
\.
COPY reconciling_expression (id, obj_version, reconciling_expression_expression, reconciling_expression_type, reconciling_expression_applied_to_canonical_model_construct, reconciling_expression_join_pred_1, reconciling_expression_join_pred_2, reconciling_expression_selection_target_super_abstract) FROM '$$PATH$$/2373.dat';

--
-- Data for Name: reduceoperator; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY reduceoperator (id, obj_version, construct_is_user_specified, mmc_dataspace_id, mapping_operator_and_or, mapping_operator_variable_name, mapping_operator_data_source_id, mapping_operator_lhs_input_operator_id, mapping_operator_mapping_id, reconcilingexpression_id, mapping_operator_result_type_id, mapping_operator_rhs_input_operator_id) FROM stdin;
\.
COPY reduceoperator (id, obj_version, construct_is_user_specified, mmc_dataspace_id, mapping_operator_and_or, mapping_operator_variable_name, mapping_operator_data_source_id, mapping_operator_lhs_input_operator_id, mapping_operator_mapping_id, reconcilingexpression_id, mapping_operator_result_type_id, mapping_operator_rhs_input_operator_id) FROM '$$PATH$$/2383.dat';

--
-- Data for Name: renameoperator; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY renameoperator (id, obj_version, construct_is_user_specified, mmc_dataspace_id, mapping_operator_and_or, mapping_operator_variable_name, mapping_operator_data_source_id, mapping_operator_lhs_input_operator_id, mapping_operator_mapping_id, reconcilingexpression_id, mapping_operator_result_type_id, mapping_operator_rhs_input_operator_id, rename_new_name, rename_canonical_model_construct_id) FROM stdin;
\.
COPY renameoperator (id, obj_version, construct_is_user_specified, mmc_dataspace_id, mapping_operator_and_or, mapping_operator_variable_name, mapping_operator_data_source_id, mapping_operator_lhs_input_operator_id, mapping_operator_mapping_id, reconcilingexpression_id, mapping_operator_result_type_id, mapping_operator_rhs_input_operator_id, rename_new_name, rename_canonical_model_construct_id) FROM '$$PATH$$/2384.dat';

--
-- Data for Name: result_field_result_values; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY result_field_result_values (id, obj_version, construct_is_user_specified, mmc_dataspace_id, result_field_name, result_value_value) FROM stdin;
\.
COPY result_field_result_values (id, obj_version, construct_is_user_specified, mmc_dataspace_id, result_field_name, result_value_value) FROM '$$PATH$$/2375.dat';

--
-- Data for Name: result_fields; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY result_fields (id, obj_version, result_field_name, result_field_type, result_field_index, construct_id) FROM stdin;
\.
COPY result_fields (id, obj_version, result_field_name, result_field_type, result_field_index, construct_id) FROM '$$PATH$$/2374.dat';

--
-- Data for Name: result_instance_mapping; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY result_instance_mapping (result_instance_id, mapping_id) FROM stdin;
\.
COPY result_instance_mapping (result_instance_id, mapping_id) FROM '$$PATH$$/2377.dat';

--
-- Data for Name: result_instance_result_values; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY result_instance_result_values (result_instance_id, result_field_result_values_id) FROM stdin;
\.
COPY result_instance_result_values (result_instance_id, result_field_result_values_id) FROM '$$PATH$$/2378.dat';

--
-- Data for Name: result_instances; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY result_instances (id, obj_version, construct_is_user_specified, mmc_dataspace_id, result_instance_query_id, result_type_id) FROM stdin;
\.
COPY result_instances (id, obj_version, construct_is_user_specified, mmc_dataspace_id, result_instance_query_id, result_type_id) FROM '$$PATH$$/2376.dat';

--
-- Data for Name: result_type_result_fields; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY result_type_result_fields (result_type_id, result_field_id) FROM stdin;
\.
COPY result_type_result_fields (result_type_id, result_field_id) FROM '$$PATH$$/2380.dat';

--
-- Data for Name: result_types; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY result_types (id, obj_version) FROM stdin;
\.
COPY result_types (id, obj_version) FROM '$$PATH$$/2379.dat';

--
-- Data for Name: role_users; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY role_users (role_id, user_id) FROM stdin;
\.
COPY role_users (role_id, user_id) FROM '$$PATH$$/2382.dat';

--
-- Data for Name: roles; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY roles (id, obj_version, role_role_type) FROM stdin;
\.
COPY roles (id, obj_version, role_role_type) FROM '$$PATH$$/2381.dat';

--
-- Data for Name: sample_kde; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY sample_kde (id, obj_version, sample_value, estimator_id) FROM stdin;
\.
COPY sample_kde (id, obj_version, sample_value, estimator_id) FROM '$$PATH$$/2385.dat';

--
-- Data for Name: scanoperator; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY scanoperator (id, obj_version, construct_is_user_specified, mmc_dataspace_id, mapping_operator_and_or, mapping_operator_variable_name, mapping_operator_data_source_id, mapping_operator_lhs_input_operator_id, mapping_operator_mapping_id, reconcilingexpression_id, mapping_operator_result_type_id, mapping_operator_rhs_input_operator_id, scan_super_abstract_id) FROM stdin;
\.
COPY scanoperator (id, obj_version, construct_is_user_specified, mmc_dataspace_id, mapping_operator_and_or, mapping_operator_variable_name, mapping_operator_data_source_id, mapping_operator_lhs_input_operator_id, mapping_operator_mapping_id, reconcilingexpression_id, mapping_operator_result_type_id, mapping_operator_rhs_input_operator_id, scan_super_abstract_id) FROM '$$PATH$$/2395.dat';

--
-- Data for Name: schemas; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY schemas (id, obj_version, construct_is_user_specified, mmc_dataspace_id, model_type, schema_name, data_source_id) FROM stdin;
\.
COPY schemas (id, obj_version, construct_is_user_specified, mmc_dataspace_id, model_type, schema_name, data_source_id) FROM '$$PATH$$/2386.dat';

--
-- Data for Name: schematic_correspondence_reconciling_expressions_1; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY schematic_correspondence_reconciling_expressions_1 (schematic_correspondence_id, reconciling_expression_id) FROM stdin;
\.
COPY schematic_correspondence_reconciling_expressions_1 (schematic_correspondence_id, reconciling_expression_id) FROM '$$PATH$$/2390.dat';

--
-- Data for Name: schematic_correspondence_reconciling_expressions_2; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY schematic_correspondence_reconciling_expressions_2 (schematic_correspondence_id, reconciling_expression_id) FROM stdin;
\.
COPY schematic_correspondence_reconciling_expressions_2 (schematic_correspondence_id, reconciling_expression_id) FROM '$$PATH$$/2391.dat';

--
-- Data for Name: schematic_correspondences; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY schematic_correspondences (id, obj_version, construct_is_user_specified, mmc_dataspace_id, cardinality_type, construct_related_schematic_correspondence_type, schematic_correspondence_description, parameter_direction, schematic_correspondence_name, schematic_correspondence_type, schematic_correspondence_short_name, parent_schematic_correspondence_id) FROM stdin;
\.
COPY schematic_correspondences (id, obj_version, construct_is_user_specified, mmc_dataspace_id, cardinality_type, construct_related_schematic_correspondence_type, schematic_correspondence_description, parameter_direction, schematic_correspondence_name, schematic_correspondence_type, schematic_correspondence_short_name, parent_schematic_correspondence_id) FROM '$$PATH$$/2387.dat';

--
-- Data for Name: schematic_correspondences_to_mapping_provenance; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY schematic_correspondences_to_mapping_provenance (id, obj_version, mapping_id) FROM stdin;
\.
COPY schematic_correspondences_to_mapping_provenance (id, obj_version, mapping_id) FROM '$$PATH$$/2388.dat';

--
-- Data for Name: schematic_correspondences_utilised_for_mappings_provenance; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY schematic_correspondences_utilised_for_mappings_provenance (provenance_id, schematic_correspondence_id) FROM stdin;
\.
COPY schematic_correspondences_utilised_for_mappings_provenance (provenance_id, schematic_correspondence_id) FROM '$$PATH$$/2389.dat';

--
-- Data for Name: setoperator; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY setoperator (id, obj_version, construct_is_user_specified, mmc_dataspace_id, mapping_operator_and_or, mapping_operator_variable_name, mapping_operator_data_source_id, mapping_operator_lhs_input_operator_id, mapping_operator_mapping_id, reconcilingexpression_id, mapping_operator_result_type_id, mapping_operator_rhs_input_operator_id, setop_type) FROM stdin;
\.
COPY setoperator (id, obj_version, construct_is_user_specified, mmc_dataspace_id, mapping_operator_and_or, mapping_operator_variable_name, mapping_operator_data_source_id, mapping_operator_lhs_input_operator_id, mapping_operator_mapping_id, reconcilingexpression_id, mapping_operator_result_type_id, mapping_operator_rhs_input_operator_id, setop_type) FROM '$$PATH$$/2396.dat';

--
-- Data for Name: super_abstracts; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY super_abstracts (id, obj_version, construct_is_user_specified, mmc_dataspace_id, canonical_model_construct_is_global, canonical_model_construct_is_virtual, canonical_model_construct_name, canonical_model_construct_type, canonical_model_construct_schema_id, super_abstract_cardinality, super_abstract_midst_super_model_type, super_abstract_model_specific_type, parent_super_abstract_id) FROM stdin;
\.
COPY super_abstracts (id, obj_version, construct_is_user_specified, mmc_dataspace_id, canonical_model_construct_is_global, canonical_model_construct_is_virtual, canonical_model_construct_name, canonical_model_construct_type, canonical_model_construct_schema_id, super_abstract_cardinality, super_abstract_midst_super_model_type, super_abstract_model_specific_type, parent_super_abstract_id) FROM '$$PATH$$/2392.dat';

--
-- Data for Name: super_lexicals; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY super_lexicals (id, obj_version, construct_is_user_specified, mmc_dataspace_id, canonical_model_construct_is_global, canonical_model_construct_is_virtual, canonical_model_construct_name, canonical_model_construct_type, canonical_model_construct_schema_id, super_lexical_data_type, super_lexical_is_identifier, super_lexical_is_nullable, super_lexical_is_optional, super_lexical_max_value_size, super_lexical_midst_super_model_type, super_lexical_model_specific_type, super_lexical_number_of_distinct_values, super_lexical_parent_super_abstract_id, parent_super_lexical_id, parent_super_relationship_id, super_lexical_id, mapkey) FROM stdin;
\.
COPY super_lexicals (id, obj_version, construct_is_user_specified, mmc_dataspace_id, canonical_model_construct_is_global, canonical_model_construct_is_virtual, canonical_model_construct_name, canonical_model_construct_type, canonical_model_construct_schema_id, super_lexical_data_type, super_lexical_is_identifier, super_lexical_is_nullable, super_lexical_is_optional, super_lexical_max_value_size, super_lexical_midst_super_model_type, super_lexical_model_specific_type, super_lexical_number_of_distinct_values, super_lexical_parent_super_abstract_id, parent_super_lexical_id, parent_super_relationship_id, super_lexical_id, mapkey) FROM '$$PATH$$/2393.dat';

--
-- Data for Name: super_relationships; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY super_relationships (id, obj_version, construct_is_user_specified, mmc_dataspace_id, canonical_model_construct_is_global, canonical_model_construct_is_virtual, canonical_model_construct_name, canonical_model_construct_type, canonical_model_construct_schema_id, super_relationship_midst_super_model_type, super_relationship_model_specific_type, super_relationship_type, super_relationship_generalised_super_abstract_id) FROM stdin;
\.
COPY super_relationships (id, obj_version, construct_is_user_specified, mmc_dataspace_id, canonical_model_construct_is_global, canonical_model_construct_is_virtual, canonical_model_construct_name, canonical_model_construct_type, canonical_model_construct_schema_id, super_relationship_midst_super_model_type, super_relationship_model_specific_type, super_relationship_type, super_relationship_generalised_super_abstract_id) FROM '$$PATH$$/2394.dat';

--
-- Data for Name: temp_construct; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY temp_construct (id, obj_version, construct_is_user_specified, mmc_dataspace_id, canonical_model_construct_is_global, canonical_model_construct_is_virtual, canonical_model_construct_name, canonical_model_construct_type, canonical_model_construct_schema_id) FROM stdin;
\.
COPY temp_construct (id, obj_version, construct_is_user_specified, mmc_dataspace_id, canonical_model_construct_is_global, canonical_model_construct_is_virtual, canonical_model_construct_name, canonical_model_construct_type, canonical_model_construct_schema_id) FROM '$$PATH$$/2397.dat';

--
-- Data for Name: typecastoperator; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY typecastoperator (id, obj_version, construct_is_user_specified, mmc_dataspace_id, mapping_operator_and_or, mapping_operator_variable_name, mapping_operator_data_source_id, mapping_operator_lhs_input_operator_id, mapping_operator_mapping_id, reconcilingexpression_id, mapping_operator_result_type_id, mapping_operator_rhs_input_operator_id, type_cast_new_type, type_cast_canonical_model_construct_id) FROM stdin;
\.
COPY typecastoperator (id, obj_version, construct_is_user_specified, mmc_dataspace_id, mapping_operator_and_or, mapping_operator_variable_name, mapping_operator_data_source_id, mapping_operator_lhs_input_operator_id, mapping_operator_mapping_id, reconcilingexpression_id, mapping_operator_result_type_id, mapping_operator_rhs_input_operator_id, type_cast_new_type, type_cast_canonical_model_construct_id) FROM '$$PATH$$/2398.dat';

--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY users (id, obj_version, accept_terms, date_created, email, first_name, institution_name, last_name, password, user_name) FROM stdin;
\.
COPY users (id, obj_version, accept_terms, date_created, email, first_name, institution_name, last_name, password, user_name) FROM '$$PATH$$/2399.dat';

--
-- Name: annotation_annotated_constructs_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY annotation_annotated_constructs
    ADD CONSTRAINT annotation_annotated_constructs_pkey PRIMARY KEY (annotation_id, annotated_construct_id);


--
-- Name: annotation_constraining_constructs_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY annotation_constraining_constructs
    ADD CONSTRAINT annotation_constraining_constructs_pkey PRIMARY KEY (annotation_id, constraining_construct_id);


--
-- Name: annotations_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY annotations
    ADD CONSTRAINT annotations_pkey PRIMARY KEY (id);


--
-- Name: constructs_morphisms_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY constructs_morphisms
    ADD CONSTRAINT constructs_morphisms_pkey PRIMARY KEY (construct_id, morphism_id);


--
-- Name: datasources_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY datasources
    ADD CONSTRAINT datasources_pkey PRIMARY KEY (id);


--
-- Name: dataspace_datasources_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY dataspace_datasources
    ADD CONSTRAINT dataspace_datasources_pkey PRIMARY KEY (dataspace_id, datasource_id);


--
-- Name: dataspace_schemas_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY dataspace_schemas
    ADD CONSTRAINT dataspace_schemas_pkey PRIMARY KEY (dataspace_id, schema_id);


--
-- Name: dataspace_users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY dataspace_users
    ADD CONSTRAINT dataspace_users_pkey PRIMARY KEY (dataspace_id, user_id);


--
-- Name: dataspaces_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY dataspaces
    ADD CONSTRAINT dataspaces_pkey PRIMARY KEY (id);


--
-- Name: joinoperator_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY joinoperator
    ADD CONSTRAINT joinoperator_pkey PRIMARY KEY (id);


--
-- Name: kernel_density_estimators_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY kernel_density_estimators
    ADD CONSTRAINT kernel_density_estimators_pkey PRIMARY KEY (id);


--
-- Name: mappings_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY mappings
    ADD CONSTRAINT mappings_pkey PRIMARY KEY (id);


--
-- Name: morphism_constructs1_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY morphism_constructs1
    ADD CONSTRAINT morphism_constructs1_pkey PRIMARY KEY (morphism_id, construct1_id);


--
-- Name: morphism_constructs2_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY morphism_constructs2
    ADD CONSTRAINT morphism_constructs2_pkey PRIMARY KEY (morphism_id, construct2_id);


--
-- Name: one_to_one_matchings_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY one_to_one_matchings
    ADD CONSTRAINT one_to_one_matchings_pkey PRIMARY KEY (id);


--
-- Name: ontology_terms_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY ontology_terms
    ADD CONSTRAINT ontology_terms_pkey PRIMARY KEY (id);


--
-- Name: parameter_applied_to_construct_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY parameter_applied_to_construct
    ADD CONSTRAINT parameter_applied_to_construct_pkey PRIMARY KEY (parameter_id, construct_id);


--
-- Name: parameters_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY parameters
    ADD CONSTRAINT parameters_pkey PRIMARY KEY (id);


--
-- Name: participation_of_cmc_in_super_relationship_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY participation_of_cmc_in_super_relationship
    ADD CONSTRAINT participation_of_cmc_in_super_relationship_pkey PRIMARY KEY (id);


--
-- Name: participation_specifying_super_lexical_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY participation_specifying_super_lexical
    ADD CONSTRAINT participation_specifying_super_lexical_pkey PRIMARY KEY (super_lexical_id, participation_in_super_relationship_id);


--
-- Name: predicates_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY predicates
    ADD CONSTRAINT predicates_pkey PRIMARY KEY (id);


--
-- Name: properties_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY properties
    ADD CONSTRAINT properties_pkey PRIMARY KEY (id);


--
-- Name: queries_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY queries
    ADD CONSTRAINT queries_pkey PRIMARY KEY (id);


--
-- Name: query_datasources_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY query_datasources
    ADD CONSTRAINT query_datasources_pkey PRIMARY KEY (query_id, datasource_id);


--
-- Name: query_result_mapping_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY query_result_mapping
    ADD CONSTRAINT query_result_mapping_pkey PRIMARY KEY (query_result_id, mapping_id);


--
-- Name: query_result_schema_of_data_source_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY query_result_schema_of_data_source
    ADD CONSTRAINT query_result_schema_of_data_source_pkey PRIMARY KEY (query_result_id, schema_of_data_source_id);


--
-- Name: query_results_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY query_results
    ADD CONSTRAINT query_results_pkey PRIMARY KEY (id);


--
-- Name: query_schemas_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY query_schemas
    ADD CONSTRAINT query_schemas_pkey PRIMARY KEY (query_id, schema_id);


--
-- Name: query_super_abstracts_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY query_super_abstracts
    ADD CONSTRAINT query_super_abstracts_pkey PRIMARY KEY (query_id, super_abstract_id);


--
-- Name: reconciling_expression_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY reconciling_expression
    ADD CONSTRAINT reconciling_expression_pkey PRIMARY KEY (id);


--
-- Name: reduceoperator_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY reduceoperator
    ADD CONSTRAINT reduceoperator_pkey PRIMARY KEY (id);


--
-- Name: renameoperator_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY renameoperator
    ADD CONSTRAINT renameoperator_pkey PRIMARY KEY (id);


--
-- Name: result_field_result_values_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY result_field_result_values
    ADD CONSTRAINT result_field_result_values_pkey PRIMARY KEY (id);


--
-- Name: result_fields_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY result_fields
    ADD CONSTRAINT result_fields_pkey PRIMARY KEY (id);


--
-- Name: result_instance_mapping_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY result_instance_mapping
    ADD CONSTRAINT result_instance_mapping_pkey PRIMARY KEY (result_instance_id, mapping_id);


--
-- Name: result_instance_result_values_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY result_instance_result_values
    ADD CONSTRAINT result_instance_result_values_pkey PRIMARY KEY (result_instance_id, result_field_result_values_id);


--
-- Name: result_instances_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY result_instances
    ADD CONSTRAINT result_instances_pkey PRIMARY KEY (id);


--
-- Name: result_type_result_fields_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY result_type_result_fields
    ADD CONSTRAINT result_type_result_fields_pkey PRIMARY KEY (result_type_id, result_field_id);


--
-- Name: result_type_result_fields_result_field_id_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY result_type_result_fields
    ADD CONSTRAINT result_type_result_fields_result_field_id_key UNIQUE (result_field_id);


--
-- Name: result_types_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY result_types
    ADD CONSTRAINT result_types_pkey PRIMARY KEY (id);


--
-- Name: role_users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY role_users
    ADD CONSTRAINT role_users_pkey PRIMARY KEY (role_id, user_id);


--
-- Name: roles_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY roles
    ADD CONSTRAINT roles_pkey PRIMARY KEY (id);


--
-- Name: sample_kde_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY sample_kde
    ADD CONSTRAINT sample_kde_pkey PRIMARY KEY (id);


--
-- Name: scanoperator_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY scanoperator
    ADD CONSTRAINT scanoperator_pkey PRIMARY KEY (id);


--
-- Name: schemas_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY schemas
    ADD CONSTRAINT schemas_pkey PRIMARY KEY (id);


--
-- Name: schematic_correspondence_reconci_reconciling_expression_id_key1; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY schematic_correspondence_reconciling_expressions_2
    ADD CONSTRAINT schematic_correspondence_reconci_reconciling_expression_id_key1 UNIQUE (reconciling_expression_id);


--
-- Name: schematic_correspondence_reconcil_reconciling_expression_id_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY schematic_correspondence_reconciling_expressions_1
    ADD CONSTRAINT schematic_correspondence_reconcil_reconciling_expression_id_key UNIQUE (reconciling_expression_id);


--
-- Name: schematic_correspondence_reconciling_expressions_1_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY schematic_correspondence_reconciling_expressions_1
    ADD CONSTRAINT schematic_correspondence_reconciling_expressions_1_pkey PRIMARY KEY (schematic_correspondence_id, reconciling_expression_id);


--
-- Name: schematic_correspondence_reconciling_expressions_2_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY schematic_correspondence_reconciling_expressions_2
    ADD CONSTRAINT schematic_correspondence_reconciling_expressions_2_pkey PRIMARY KEY (schematic_correspondence_id, reconciling_expression_id);


--
-- Name: schematic_correspondences_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY schematic_correspondences
    ADD CONSTRAINT schematic_correspondences_pkey PRIMARY KEY (id);


--
-- Name: schematic_correspondences_to_mapping_provenance_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY schematic_correspondences_to_mapping_provenance
    ADD CONSTRAINT schematic_correspondences_to_mapping_provenance_pkey PRIMARY KEY (id);


--
-- Name: schematic_correspondences_utilised_for_mappings_provenance_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY schematic_correspondences_utilised_for_mappings_provenance
    ADD CONSTRAINT schematic_correspondences_utilised_for_mappings_provenance_pkey PRIMARY KEY (provenance_id, schematic_correspondence_id);


--
-- Name: setoperator_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY setoperator
    ADD CONSTRAINT setoperator_pkey PRIMARY KEY (id);


--
-- Name: super_abstracts_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY super_abstracts
    ADD CONSTRAINT super_abstracts_pkey PRIMARY KEY (id);


--
-- Name: super_lexicals_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY super_lexicals
    ADD CONSTRAINT super_lexicals_pkey PRIMARY KEY (id);


--
-- Name: super_relationships_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY super_relationships
    ADD CONSTRAINT super_relationships_pkey PRIMARY KEY (id);


--
-- Name: temp_construct_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY temp_construct
    ADD CONSTRAINT temp_construct_pkey PRIMARY KEY (id);


--
-- Name: typecastoperator_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY typecastoperator
    ADD CONSTRAINT typecastoperator_pkey PRIMARY KEY (id);


--
-- Name: users_email_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY users
    ADD CONSTRAINT users_email_key UNIQUE (email);


--
-- Name: users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: users_user_name_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY users
    ADD CONSTRAINT users_user_name_key UNIQUE (user_name);


--
-- Name: fk1c06aa3b5914c355; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY query_schemas
    ADD CONSTRAINT fk1c06aa3b5914c355 FOREIGN KEY (query_id) REFERENCES queries(id);


--
-- Name: fk1c06aa3b880c25a4; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY query_schemas
    ADD CONSTRAINT fk1c06aa3b880c25a4 FOREIGN KEY (schema_id) REFERENCES schemas(id);


--
-- Name: fk21d2c10f880c25a4; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY dataspace_schemas
    ADD CONSTRAINT fk21d2c10f880c25a4 FOREIGN KEY (schema_id) REFERENCES schemas(id);


--
-- Name: fk21d2c10fa5346195; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY dataspace_schemas
    ADD CONSTRAINT fk21d2c10fa5346195 FOREIGN KEY (dataspace_id) REFERENCES dataspaces(id);


--
-- Name: fk31aec838398bd4f0; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY result_type_result_fields
    ADD CONSTRAINT fk31aec838398bd4f0 FOREIGN KEY (result_type_id) REFERENCES result_types(id);


--
-- Name: fk31aec838e80d60c4; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY result_type_result_fields
    ADD CONSTRAINT fk31aec838e80d60c4 FOREIGN KEY (result_field_id) REFERENCES result_fields(id);


--
-- Name: fk35575e8b58e9243e; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY parameter_applied_to_construct
    ADD CONSTRAINT fk35575e8b58e9243e FOREIGN KEY (parameter_id) REFERENCES parameters(id);


--
-- Name: fk3d200fdb8af976df; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY query_result_schema_of_data_source
    ADD CONSTRAINT fk3d200fdb8af976df FOREIGN KEY (schema_of_data_source_id) REFERENCES schemas(id);


--
-- Name: fk3d200fdba8a2e650; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY query_result_schema_of_data_source
    ADD CONSTRAINT fk3d200fdba8a2e650 FOREIGN KEY (query_result_id) REFERENCES query_results(id);


--
-- Name: fk47c8342045ade094; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY annotation_constraining_constructs
    ADD CONSTRAINT fk47c8342045ade094 FOREIGN KEY (annotation_id) REFERENCES annotations(id);


--
-- Name: fk482186f65914c355; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY query_super_abstracts
    ADD CONSTRAINT fk482186f65914c355 FOREIGN KEY (query_id) REFERENCES queries(id);


--
-- Name: fk482186f6bc26ce98; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY query_super_abstracts
    ADD CONSTRAINT fk482186f6bc26ce98 FOREIGN KEY (super_abstract_id) REFERENCES super_abstracts(id);


--
-- Name: fk52a767bc7f33a796; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY result_instance_result_values
    ADD CONSTRAINT fk52a767bc7f33a796 FOREIGN KEY (result_field_result_values_id) REFERENCES result_field_result_values(id);


--
-- Name: fk52a767bcdebf6810; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY result_instance_result_values
    ADD CONSTRAINT fk52a767bcdebf6810 FOREIGN KEY (result_instance_id) REFERENCES result_instances(id);


--
-- Name: fk5371733ec94546c5; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schematic_correspondences_to_mapping_provenance
    ADD CONSTRAINT fk5371733ec94546c5 FOREIGN KEY (mapping_id) REFERENCES mappings(id);


--
-- Name: fk57a47cb345ade094; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY annotation_annotated_constructs
    ADD CONSTRAINT fk57a47cb345ade094 FOREIGN KEY (annotation_id) REFERENCES annotations(id);


--
-- Name: fk6218799c19d4357a; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY predicates
    ADD CONSTRAINT fk6218799c19d4357a FOREIGN KEY (scan_predicate_id) REFERENCES scanoperator(id);


--
-- Name: fk6218799cfd52399a; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY predicates
    ADD CONSTRAINT fk6218799cfd52399a FOREIGN KEY (join_predicate_id) REFERENCES joinoperator(id);


--
-- Name: fk660937bd79ff3e2e; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ontology_term_enum_values
    ADD CONSTRAINT fk660937bd79ff3e2e FOREIGN KEY (enum_id) REFERENCES ontology_terms(id);


--
-- Name: fk757b51091820cbd9; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schematic_correspondences_utilised_for_mappings_provenance
    ADD CONSTRAINT fk757b51091820cbd9 FOREIGN KEY (schematic_correspondence_id) REFERENCES schematic_correspondences(id);


--
-- Name: fk757b51097eb1d99; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schematic_correspondences_utilised_for_mappings_provenance
    ADD CONSTRAINT fk757b51097eb1d99 FOREIGN KEY (provenance_id) REFERENCES schematic_correspondences_to_mapping_provenance(id);


--
-- Name: fk90a07c6ba5346195; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY dataspace_datasources
    ADD CONSTRAINT fk90a07c6ba5346195 FOREIGN KEY (dataspace_id) REFERENCES dataspaces(id);


--
-- Name: fk90a07c6bfee88a44; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY dataspace_datasources
    ADD CONSTRAINT fk90a07c6bfee88a44 FOREIGN KEY (datasource_id) REFERENCES datasources(id);


--
-- Name: fk_annotation_ontology_term_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY annotations
    ADD CONSTRAINT fk_annotation_ontology_term_id FOREIGN KEY (annotation_ontology_term_id) REFERENCES ontology_terms(id);


--
-- Name: fk_annotation_user_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY annotations
    ADD CONSTRAINT fk_annotation_user_id FOREIGN KEY (annotation_user_id) REFERENCES users(id);


--
-- Name: fk_canonical_model_construct_schema_id3d29608d; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY super_abstracts
    ADD CONSTRAINT fk_canonical_model_construct_schema_id3d29608d FOREIGN KEY (canonical_model_construct_schema_id) REFERENCES schemas(id);


--
-- Name: fk_canonical_model_construct_schema_id63923737; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY super_relationships
    ADD CONSTRAINT fk_canonical_model_construct_schema_id63923737 FOREIGN KEY (canonical_model_construct_schema_id) REFERENCES schemas(id);


--
-- Name: fk_canonical_model_construct_schema_id7232f10c; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY temp_construct
    ADD CONSTRAINT fk_canonical_model_construct_schema_id7232f10c FOREIGN KEY (canonical_model_construct_schema_id) REFERENCES schemas(id);


--
-- Name: fk_canonical_model_construct_schema_idedb2b7f3; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY super_lexicals
    ADD CONSTRAINT fk_canonical_model_construct_schema_idedb2b7f3 FOREIGN KEY (canonical_model_construct_schema_id) REFERENCES schemas(id);


--
-- Name: fk_mapping_operator_result_type_id3e49da61; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY scanoperator
    ADD CONSTRAINT fk_mapping_operator_result_type_id3e49da61 FOREIGN KEY (mapping_operator_result_type_id) REFERENCES result_types(id);


--
-- Name: fk_mapping_operator_result_type_id5e11884a; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY reduceoperator
    ADD CONSTRAINT fk_mapping_operator_result_type_id5e11884a FOREIGN KEY (mapping_operator_result_type_id) REFERENCES result_types(id);


--
-- Name: fk_mapping_operator_result_type_id94027222; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY renameoperator
    ADD CONSTRAINT fk_mapping_operator_result_type_id94027222 FOREIGN KEY (mapping_operator_result_type_id) REFERENCES result_types(id);


--
-- Name: fk_mapping_operator_result_type_id960ef40e; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY joinoperator
    ADD CONSTRAINT fk_mapping_operator_result_type_id960ef40e FOREIGN KEY (mapping_operator_result_type_id) REFERENCES result_types(id);


--
-- Name: fk_mapping_operator_result_type_ida776797d; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY typecastoperator
    ADD CONSTRAINT fk_mapping_operator_result_type_ida776797d FOREIGN KEY (mapping_operator_result_type_id) REFERENCES result_types(id);


--
-- Name: fk_mapping_operator_result_type_idbc266f06; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY setoperator
    ADD CONSTRAINT fk_mapping_operator_result_type_idbc266f06 FOREIGN KEY (mapping_operator_result_type_id) REFERENCES result_types(id);


--
-- Name: fk_mapping_query1_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY mappings
    ADD CONSTRAINT fk_mapping_query1_id FOREIGN KEY (mapping_query1_id) REFERENCES queries(id);


--
-- Name: fk_mapping_query2_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY mappings
    ADD CONSTRAINT fk_mapping_query2_id FOREIGN KEY (mapping_query2_id) REFERENCES queries(id);


--
-- Name: fk_mmc_dataspace_id26f08d46; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY participation_of_cmc_in_super_relationship
    ADD CONSTRAINT fk_mmc_dataspace_id26f08d46 FOREIGN KEY (mmc_dataspace_id) REFERENCES dataspaces(id);


--
-- Name: fk_mmc_dataspace_id4c4e2cae; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY datasources
    ADD CONSTRAINT fk_mmc_dataspace_id4c4e2cae FOREIGN KEY (mmc_dataspace_id) REFERENCES dataspaces(id);


--
-- Name: fk_mmc_dataspace_id51d76346; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY queries
    ADD CONSTRAINT fk_mmc_dataspace_id51d76346 FOREIGN KEY (mmc_dataspace_id) REFERENCES dataspaces(id);


--
-- Name: fk_mmc_dataspace_id558d94a23d29608d; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY super_abstracts
    ADD CONSTRAINT fk_mmc_dataspace_id558d94a23d29608d FOREIGN KEY (mmc_dataspace_id) REFERENCES dataspaces(id);


--
-- Name: fk_mmc_dataspace_id558d94a263923737; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY super_relationships
    ADD CONSTRAINT fk_mmc_dataspace_id558d94a263923737 FOREIGN KEY (mmc_dataspace_id) REFERENCES dataspaces(id);


--
-- Name: fk_mmc_dataspace_id558d94a27232f10c; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY temp_construct
    ADD CONSTRAINT fk_mmc_dataspace_id558d94a27232f10c FOREIGN KEY (mmc_dataspace_id) REFERENCES dataspaces(id);


--
-- Name: fk_mmc_dataspace_id558d94a2edb2b7f3; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY super_lexicals
    ADD CONSTRAINT fk_mmc_dataspace_id558d94a2edb2b7f3 FOREIGN KEY (mmc_dataspace_id) REFERENCES dataspaces(id);


--
-- Name: fk_mmc_dataspace_id582304bc; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY result_instances
    ADD CONSTRAINT fk_mmc_dataspace_id582304bc FOREIGN KEY (mmc_dataspace_id) REFERENCES dataspaces(id);


--
-- Name: fk_mmc_dataspace_id6218799c; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY predicates
    ADD CONSTRAINT fk_mmc_dataspace_id6218799c FOREIGN KEY (mmc_dataspace_id) REFERENCES dataspaces(id);


--
-- Name: fk_mmc_dataspace_id72e875bd; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY result_field_result_values
    ADD CONSTRAINT fk_mmc_dataspace_id72e875bd FOREIGN KEY (mmc_dataspace_id) REFERENCES dataspaces(id);


--
-- Name: fk_mmc_dataspace_id9d110ad2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemas
    ADD CONSTRAINT fk_mmc_dataspace_id9d110ad2 FOREIGN KEY (mmc_dataspace_id) REFERENCES dataspaces(id);


--
-- Name: fk_mmc_dataspace_ide9fcaf723e49da61; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY scanoperator
    ADD CONSTRAINT fk_mmc_dataspace_ide9fcaf723e49da61 FOREIGN KEY (mmc_dataspace_id) REFERENCES dataspaces(id);


--
-- Name: fk_mmc_dataspace_ide9fcaf725e11884a; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY reduceoperator
    ADD CONSTRAINT fk_mmc_dataspace_ide9fcaf725e11884a FOREIGN KEY (mmc_dataspace_id) REFERENCES dataspaces(id);


--
-- Name: fk_mmc_dataspace_ide9fcaf7294027222; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY renameoperator
    ADD CONSTRAINT fk_mmc_dataspace_ide9fcaf7294027222 FOREIGN KEY (mmc_dataspace_id) REFERENCES dataspaces(id);


--
-- Name: fk_mmc_dataspace_ide9fcaf72960ef40e; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY joinoperator
    ADD CONSTRAINT fk_mmc_dataspace_ide9fcaf72960ef40e FOREIGN KEY (mmc_dataspace_id) REFERENCES dataspaces(id);


--
-- Name: fk_mmc_dataspace_ide9fcaf72a776797d; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY typecastoperator
    ADD CONSTRAINT fk_mmc_dataspace_ide9fcaf72a776797d FOREIGN KEY (mmc_dataspace_id) REFERENCES dataspaces(id);


--
-- Name: fk_mmc_dataspace_ide9fcaf72bc266f06; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY setoperator
    ADD CONSTRAINT fk_mmc_dataspace_ide9fcaf72bc266f06 FOREIGN KEY (mmc_dataspace_id) REFERENCES dataspaces(id);


--
-- Name: fk_mmc_dataspace_ideb2c673f; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY query_results
    ADD CONSTRAINT fk_mmc_dataspace_ideb2c673f FOREIGN KEY (mmc_dataspace_id) REFERENCES dataspaces(id);


--
-- Name: fk_mmc_dataspace_idf771ce9b158d101d25461f52; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY one_to_one_matchings
    ADD CONSTRAINT fk_mmc_dataspace_idf771ce9b158d101d25461f52 FOREIGN KEY (mmc_dataspace_id) REFERENCES dataspaces(id);


--
-- Name: fk_mmc_dataspace_idf771ce9b3a3cf165; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY mappings
    ADD CONSTRAINT fk_mmc_dataspace_idf771ce9b3a3cf165 FOREIGN KEY (mmc_dataspace_id) REFERENCES dataspaces(id);


--
-- Name: fk_mmc_dataspace_idf771ce9b4971e6e7; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schematic_correspondences
    ADD CONSTRAINT fk_mmc_dataspace_idf771ce9b4971e6e7 FOREIGN KEY (mmc_dataspace_id) REFERENCES dataspaces(id);


--
-- Name: fk_ontology_term_parent_ontology_term_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ontology_terms
    ADD CONSTRAINT fk_ontology_term_parent_ontology_term_id FOREIGN KEY (parent_ontology_term_id) REFERENCES ontology_terms(id);


--
-- Name: fk_operator_data_source_id3e49da61; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY scanoperator
    ADD CONSTRAINT fk_operator_data_source_id3e49da61 FOREIGN KEY (mapping_operator_data_source_id) REFERENCES datasources(id);


--
-- Name: fk_operator_data_source_id5e11884a; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY reduceoperator
    ADD CONSTRAINT fk_operator_data_source_id5e11884a FOREIGN KEY (mapping_operator_data_source_id) REFERENCES datasources(id);


--
-- Name: fk_operator_data_source_id94027222; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY renameoperator
    ADD CONSTRAINT fk_operator_data_source_id94027222 FOREIGN KEY (mapping_operator_data_source_id) REFERENCES datasources(id);


--
-- Name: fk_operator_data_source_id960ef40e; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY joinoperator
    ADD CONSTRAINT fk_operator_data_source_id960ef40e FOREIGN KEY (mapping_operator_data_source_id) REFERENCES datasources(id);


--
-- Name: fk_operator_data_source_ida776797d; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY typecastoperator
    ADD CONSTRAINT fk_operator_data_source_ida776797d FOREIGN KEY (mapping_operator_data_source_id) REFERENCES datasources(id);


--
-- Name: fk_operator_data_source_idbc266f06; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY setoperator
    ADD CONSTRAINT fk_operator_data_source_idbc266f06 FOREIGN KEY (mapping_operator_data_source_id) REFERENCES datasources(id);


--
-- Name: fk_operator_mapping_id3e49da61; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY scanoperator
    ADD CONSTRAINT fk_operator_mapping_id3e49da61 FOREIGN KEY (mapping_operator_mapping_id) REFERENCES mappings(id);


--
-- Name: fk_operator_mapping_id5e11884a; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY reduceoperator
    ADD CONSTRAINT fk_operator_mapping_id5e11884a FOREIGN KEY (mapping_operator_mapping_id) REFERENCES mappings(id);


--
-- Name: fk_operator_mapping_id94027222; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY renameoperator
    ADD CONSTRAINT fk_operator_mapping_id94027222 FOREIGN KEY (mapping_operator_mapping_id) REFERENCES mappings(id);


--
-- Name: fk_operator_mapping_id960ef40e; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY joinoperator
    ADD CONSTRAINT fk_operator_mapping_id960ef40e FOREIGN KEY (mapping_operator_mapping_id) REFERENCES mappings(id);


--
-- Name: fk_operator_mapping_ida776797d; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY typecastoperator
    ADD CONSTRAINT fk_operator_mapping_ida776797d FOREIGN KEY (mapping_operator_mapping_id) REFERENCES mappings(id);


--
-- Name: fk_operator_mapping_idbc266f06; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY setoperator
    ADD CONSTRAINT fk_operator_mapping_idbc266f06 FOREIGN KEY (mapping_operator_mapping_id) REFERENCES mappings(id);


--
-- Name: fk_parameter_schematic_correspondence_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY parameters
    ADD CONSTRAINT fk_parameter_schematic_correspondence_id FOREIGN KEY (schematic_correspondence_id) REFERENCES schematic_correspondences(id);


--
-- Name: fk_participation_super_relationship_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY participation_of_cmc_in_super_relationship
    ADD CONSTRAINT fk_participation_super_relationship_id FOREIGN KEY (super_relationship_id) REFERENCES super_relationships(id);


--
-- Name: fk_predicate_super_lexical1_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY predicates
    ADD CONSTRAINT fk_predicate_super_lexical1_id FOREIGN KEY (predicate_super_lexical1_id) REFERENCES super_lexicals(id);


--
-- Name: fk_predicate_super_lexical2_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY predicates
    ADD CONSTRAINT fk_predicate_super_lexical2_id FOREIGN KEY (predicate_super_lexical2_id) REFERENCES super_lexicals(id);


--
-- Name: fk_query_dataspace_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY queries
    ADD CONSTRAINT fk_query_dataspace_id FOREIGN KEY (query_dataspace_id) REFERENCES dataspaces(id);


--
-- Name: fk_query_result_dataspace_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY query_results
    ADD CONSTRAINT fk_query_result_dataspace_id FOREIGN KEY (query_result_dataspace_id) REFERENCES dataspaces(id);


--
-- Name: fk_query_result_query_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY query_results
    ADD CONSTRAINT fk_query_result_query_id FOREIGN KEY (query_result_query_id) REFERENCES queries(id);


--
-- Name: fk_query_result_result_type_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY query_results
    ADD CONSTRAINT fk_query_result_result_type_id FOREIGN KEY (query_result_result_type_id) REFERENCES result_types(id);


--
-- Name: fk_query_user_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY queries
    ADD CONSTRAINT fk_query_user_id FOREIGN KEY (query_user_id) REFERENCES users(id);


--
-- Name: fk_reconciling_expression_join_pred1_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY reconciling_expression
    ADD CONSTRAINT fk_reconciling_expression_join_pred1_id FOREIGN KEY (reconciling_expression_join_pred_1) REFERENCES super_abstracts(id);


--
-- Name: fk_reconciling_expression_join_pred2_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY reconciling_expression
    ADD CONSTRAINT fk_reconciling_expression_join_pred2_id FOREIGN KEY (reconciling_expression_join_pred_2) REFERENCES super_abstracts(id);


--
-- Name: fk_reconciling_expression_target_super_abstract_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY reconciling_expression
    ADD CONSTRAINT fk_reconciling_expression_target_super_abstract_id FOREIGN KEY (reconciling_expression_selection_target_super_abstract) REFERENCES super_abstracts(id);


--
-- Name: fk_result_instance_query_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY result_instances
    ADD CONSTRAINT fk_result_instance_query_id FOREIGN KEY (result_instance_query_id) REFERENCES queries(id);


--
-- Name: fk_result_instance_result_type_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY result_instances
    ADD CONSTRAINT fk_result_instance_result_type_id FOREIGN KEY (result_type_id) REFERENCES result_types(id);


--
-- Name: fk_sample_estimator_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY sample_kde
    ADD CONSTRAINT fk_sample_estimator_id FOREIGN KEY (estimator_id) REFERENCES kernel_density_estimators(id);


--
-- Name: fk_scan_super_abstract_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY scanoperator
    ADD CONSTRAINT fk_scan_super_abstract_id FOREIGN KEY (scan_super_abstract_id) REFERENCES super_abstracts(id);


--
-- Name: fk_schema_datasource_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemas
    ADD CONSTRAINT fk_schema_datasource_id FOREIGN KEY (data_source_id) REFERENCES datasources(id);


--
-- Name: fk_schematic_correspondence_parent_correspondence_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schematic_correspondences
    ADD CONSTRAINT fk_schematic_correspondence_parent_correspondence_id FOREIGN KEY (parent_schematic_correspondence_id) REFERENCES schematic_correspondences(id);


--
-- Name: fk_super_abstract_parent_super_abstract_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY super_abstracts
    ADD CONSTRAINT fk_super_abstract_parent_super_abstract_id FOREIGN KEY (parent_super_abstract_id) REFERENCES super_abstracts(id);


--
-- Name: fk_super_lexical_parent_super_abstract_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY super_lexicals
    ADD CONSTRAINT fk_super_lexical_parent_super_abstract_id FOREIGN KEY (super_lexical_parent_super_abstract_id) REFERENCES super_abstracts(id);


--
-- Name: fk_super_lexical_parent_super_lexical_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY super_lexicals
    ADD CONSTRAINT fk_super_lexical_parent_super_lexical_id FOREIGN KEY (parent_super_lexical_id) REFERENCES super_lexicals(id);


--
-- Name: fk_super_lexical_parent_super_relationship_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY super_lexicals
    ADD CONSTRAINT fk_super_lexical_parent_super_relationship_id FOREIGN KEY (parent_super_relationship_id) REFERENCES super_relationships(id);


--
-- Name: fk_super_relationship_generalised_super_abstract_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY super_relationships
    ADD CONSTRAINT fk_super_relationship_generalised_super_abstract_id FOREIGN KEY (super_relationship_generalised_super_abstract_id) REFERENCES super_abstracts(id);


--
-- Name: fkac37d9461820cbd9; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schematic_correspondence_reconciling_expressions_1
    ADD CONSTRAINT fkac37d9461820cbd9 FOREIGN KEY (schematic_correspondence_id) REFERENCES schematic_correspondences(id);


--
-- Name: fkac37d94625773a15; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schematic_correspondence_reconciling_expressions_1
    ADD CONSTRAINT fkac37d94625773a15 FOREIGN KEY (reconciling_expression_id) REFERENCES reconciling_expression(id);


--
-- Name: fkac37d9471820cbd9; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schematic_correspondence_reconciling_expressions_2
    ADD CONSTRAINT fkac37d9471820cbd9 FOREIGN KEY (schematic_correspondence_id) REFERENCES schematic_correspondences(id);


--
-- Name: fkac37d94725773a15; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schematic_correspondence_reconciling_expressions_2
    ADD CONSTRAINT fkac37d94725773a15 FOREIGN KEY (reconciling_expression_id) REFERENCES reconciling_expression(id);


--
-- Name: fkc07cc0df85bf07b8; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY role_users
    ADD CONSTRAINT fkc07cc0df85bf07b8 FOREIGN KEY (user_id) REFERENCES users(id);


--
-- Name: fkc07cc0dfe09443d8; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY role_users
    ADD CONSTRAINT fkc07cc0dfe09443d8 FOREIGN KEY (role_id) REFERENCES roles(id);


--
-- Name: fkca18b76585bf07b8; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY dataspace_users
    ADD CONSTRAINT fkca18b76585bf07b8 FOREIGN KEY (user_id) REFERENCES users(id);


--
-- Name: fkca18b765a5346195; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY dataspace_users
    ADD CONSTRAINT fkca18b765a5346195 FOREIGN KEY (dataspace_id) REFERENCES dataspaces(id);


--
-- Name: fkd0118303a8a2e650; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY query_result_mapping
    ADD CONSTRAINT fkd0118303a8a2e650 FOREIGN KEY (query_result_id) REFERENCES query_results(id);


--
-- Name: fkd0118303c94546c5; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY query_result_mapping
    ADD CONSTRAINT fkd0118303c94546c5 FOREIGN KEY (mapping_id) REFERENCES mappings(id);


--
-- Name: fkd3d32ee6c94546c5; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY result_instance_mapping
    ADD CONSTRAINT fkd3d32ee6c94546c5 FOREIGN KEY (mapping_id) REFERENCES mappings(id);


--
-- Name: fkd3d32ee6debf6810; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY result_instance_mapping
    ADD CONSTRAINT fkd3d32ee6debf6810 FOREIGN KEY (result_instance_id) REFERENCES result_instances(id);


--
-- Name: fkd87f88e056d57076; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY participation_specifying_super_lexical
    ADD CONSTRAINT fkd87f88e056d57076 FOREIGN KEY (participation_in_super_relationship_id) REFERENCES participation_of_cmc_in_super_relationship(id);


--
-- Name: fkd87f88e0900e635c; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY participation_specifying_super_lexical
    ADD CONSTRAINT fkd87f88e0900e635c FOREIGN KEY (super_lexical_id) REFERENCES super_lexicals(id);


--
-- Name: fkd9fb03cca8a2e650; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY query_result_result_instance
    ADD CONSTRAINT fkd9fb03cca8a2e650 FOREIGN KEY (query_result_id) REFERENCES query_results(id);


--
-- Name: fkd9fb03ccdebf6810; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY query_result_result_instance
    ADD CONSTRAINT fkd9fb03ccdebf6810 FOREIGN KEY (result_instance_id) REFERENCES result_instances(id);


--
-- Name: fke9fcaf724913581e3e49da61; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY scanoperator
    ADD CONSTRAINT fke9fcaf724913581e3e49da61 FOREIGN KEY (reconcilingexpression_id) REFERENCES reconciling_expression(id);


--
-- Name: fke9fcaf724913581e5e11884a; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY reduceoperator
    ADD CONSTRAINT fke9fcaf724913581e5e11884a FOREIGN KEY (reconcilingexpression_id) REFERENCES reconciling_expression(id);


--
-- Name: fke9fcaf724913581e94027222; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY renameoperator
    ADD CONSTRAINT fke9fcaf724913581e94027222 FOREIGN KEY (reconcilingexpression_id) REFERENCES reconciling_expression(id);


--
-- Name: fke9fcaf724913581e960ef40e; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY joinoperator
    ADD CONSTRAINT fke9fcaf724913581e960ef40e FOREIGN KEY (reconcilingexpression_id) REFERENCES reconciling_expression(id);


--
-- Name: fke9fcaf724913581ea776797d; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY typecastoperator
    ADD CONSTRAINT fke9fcaf724913581ea776797d FOREIGN KEY (reconcilingexpression_id) REFERENCES reconciling_expression(id);


--
-- Name: fke9fcaf724913581ebc266f06; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY setoperator
    ADD CONSTRAINT fke9fcaf724913581ebc266f06 FOREIGN KEY (reconcilingexpression_id) REFERENCES reconciling_expression(id);


--
-- Name: fkedb2b7f3192a9458; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY super_lexicals
    ADD CONSTRAINT fkedb2b7f3192a9458 FOREIGN KEY (super_lexical_id) REFERENCES reduceoperator(id);


--
-- Name: fkf23ef975914c355; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY query_datasources
    ADD CONSTRAINT fkf23ef975914c355 FOREIGN KEY (query_id) REFERENCES queries(id);


--
-- Name: fkf23ef97fee88a44; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY query_datasources
    ADD CONSTRAINT fkf23ef97fee88a44 FOREIGN KEY (datasource_id) REFERENCES datasources(id);


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         