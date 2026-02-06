# LasanhaSpec
Um projeto mais intimista, mas que não deixa de ser ambicioso para todos aqueles que gostam de carros ou que querem aprender mais sobre eles.

É uma plataforma voltada para entusiastas do mundo automotivo, das corridas e da preparação. 
Com foco em análise técnica de veículos, preparação automotiva e impacto de modificações de performance, consumo e custo, utilizando arquitetura baseada em microserviços.

O projeto nasceu da ideia do autor de sempre pensar nos carros como grandes legos alinhado ao prazer e curiosidade em programar coisas diferentes.
E como todo bom grande lasanheiro, quem não gostaria de um programa acessível de análise de performance baseado em valores e impactos reais que as modificações proporcionam para o seu possante, hein? haha





# Objetivo  


O objetivo do projeto é estudar e aplicar conceitos de:
- Arquitetura de microserviços
- Spring Boot e JAVA
- APIs REST
- Docker e CI/CD
- Cloud (AWS)



# Escopo inicial 

Nesta primeira fase, o projeto será composto por um único microserviço responsável pelo cadastro e consulta de dados técnicos de veículos.
Novos serviços serão adicionados de forma incremental conforme a evolução e necessidade do projeto.


# Tecnologias 


- JAVA 17
- Spring Boot
- Spring Web
- Spring Data JPA
- PostgresSQL
- Docker
- GitHub Actions
- AWS EC2 (planejado)



# Arquitetura inicial

Andei me informando sobre microserviços e percebi que não é todo projeto que se deve começar de cara com esse tipo de arquitetura de software, e que em muitos casos há a necessidade de maturar o projeto em um
monolitico bem estruturado e de acordo com a escalabilidade e necessidades do projeto, ir quebrando o sistema para uma nova concepção, mas como este projeto é mais como uma cobaia para fins didáticos e de diversão mesmo. Então o projeto segue uma abordagem baseada em microserviços, iniciando com um serviço central de veículos.




# Endpoints



- POST /cars — Cadastro de veículos
- GET /cars — Listagem de veículos
- GET /cars/{id} — Consulta por ID
- GET /cars/search — Busca por critérios técnicos



# Como executar 


As instruções para execução local via Docker e Maven serão adicionadas nas próximas versões do projeto.


