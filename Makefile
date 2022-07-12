# build from Dockerfile. It uses multistage build so there is no need to build the jar separately
build: 
	docker-compose build eduson-be

run:
	docker-compose up -d

login:
	docker login --username ladoandrea --password "${GITLAB_PASSWORD}" registry.gitlab.com

push:
	docker-compose push eduson-be

update:
	docker-compose pull

docker-prod-file = -f docker-compose.prod.yml
docker-file = -f docker-compose.yml

deploy = docker-compose $(docker-file) $(docker-prod-file) pull && docker-compose $(docker-file) $(docker-prod-file) up -d eduson-be

deploy-from-local = "docker-compose -f docker-compose.prod.yml -f docker-compose.yml pull && docker-compose -f docker-compose.prod.yml -f docker-compose.yml up -d eduson-be"

deploy-prod:
	$(deploy)

stop:
	docker rm -f eduson-be

provision-prod:
	scp docker-compose.yml eduson:~/docker-compose.yml
	scp docker-compose.prod.yml eduson:~/docker-compose.prod.yml

