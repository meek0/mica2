skipTests = false
mvn_exec = mvn -Dmaven.test.skip=${skipTests}
current_dir = $(shell pwd)
mica_server_home = ${current_dir}/mica-server/target/mica_server_home
mica_server_log = ${mica_server_home}/logs

help:
	@echo
	@echo "Mica Server"
	@echo
	@echo "Available make targets:"
	@echo "  all         : Clean & install all modules"
	@echo "  clean       : Clean all modules"
	@echo "  install     : Install all modules"
	@echo "  core        : Install core module"
	@echo "  search      : Install search module"
	@echo "  rest        : Install rest module"
	@echo "  angular     : Install angularjs module"
	@echo
	@echo "  run         : Run server module"
	@echo "  debug       : Debug server module on port 8000"
	@echo "  grunt       : Start grunt on port 9000"
	@echo "  npm-install : Download all NodeJS dependencies"
	@echo
	@echo "  clear-log   : Delete logs from ${mica_server_log}"
	@echo "  drop-mongo  : Drop MongoDB mica database"
	@echo
	@echo "  dependencies-tree   : Displays the dependency tree"
	@echo "  dependencies-update : Check for new dependency updates"
	@echo "  plugins-update      : Check for new plugin updates"
	@echo

all: clean install

clean:
	${mvn_exec} clean

install:
	${mvn_exec} install

core:
	cd mica-core && ${mvn_exec} install

search:
	cd mica-search && ${mvn_exec} install

rest:
	cd mica-rest && ${mvn_exec} install

angular:
	cd mica-angularjs-client && ${mvn_exec} install

run:
	cd mica-server && ${mvn_exec} spring-boot:run -DMICA_SERVER_HOME="${mica_server_home}" -DMICA_SERVER_LOG="${mica_server_log}"

debug:
	export MAVEN_OPTS=-agentlib:jdwp=transport=dt_socket,server=y,address=8000,suspend=n && \
	cd mica-server && ${mvn_exec} spring-boot:run -DMICA_SERVER_HOME="${mica_server_home}" -DMICA_SERVER_LOG="${mica_server_log}"

grunt:
	cd mica-angularjs-client && grunt server

npm-install:
	cd mica-angularjs-client && npm install

clear-log:
	rm -rf ${mica_server_log}

drop-mongo:
	mongo mica --eval "db.dropDatabase()"

dependencies-tree:
	mvn dependency:tree

dependencies-update:
	mvn versions:display-dependency-updates

plugins-update:
	mvn versions:display-plugin-updates

elasticsearch-head:
	rm -rf .work/elasticsearch-head && \
	mkdir -p .work && \
	cd .work && \
	git clone git://github.com/mobz/elasticsearch-head.git
	@echo  
	@echo "ElasticSearch-Head is available at:" 
	@echo "file://${current_dir}/.work/elasticsearch-head/index.html" 
	@echo  
