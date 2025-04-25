# prewave code challenge

## setup
1. use Java 21 Temurin
2. create DB, user-name and db-name will be "postgres"  
`docker run --name postgres_prewave -e POSTGRES_PASSWORD=mysecretpassword -p 5432:5432 -d postgres`
3. create table  
`
create table edge (
    from_id integer not null,
    to_id   integer not null,
    primary key (from_id, to_id)
);
CREATE INDEX idx_edge_to_id ON edge(to_id);
`
4. start application PrewaveApplication
5. play around with api.rest file (like postman inside IDE)
6. I did implemented 2 performance tests for 2k objects, for repo and controller separately. Please check it out.  

