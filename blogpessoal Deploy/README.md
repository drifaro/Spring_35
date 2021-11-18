<h1>Deploy do Back-end Spring no Heroku</h1>



Siga as etapas abaixo:

1. <a href="#swag">Criar a Documentação da API no Swagger</a>
2. <a href="#user">Criar um usuário padrão em memória</a>
3. <a href="#configure">Atualizar o Método configure(HttpSecurity http)</a>
4. <a href="#local">Testar o seu projeto localmente (http://localhost:8080)</a>
5. <a href="#cfhrk">Criar conta grátis no Heroku</a>
6. <a href="#node">Instalar o Node no seu computador</a>
7. <a href="#hrkcli">Instalar o Heroku Client</a>
8. <a href="#sprop">Criar o arquivo <code>system.properties</code> no seu projeto</a>
9. <a href="#pom02">Adicionar a dependência do PostgreSQL no arquivo <code>pom.xml</code> do seu projeto</a>
10. <a href="#approp">Configurar a conexão com o Banco de Dados no arquivo <code>application.properties</code> do seu projeto</a>
11. <a href="#git">Preparar o seu projeto para o Deploy com o Git</a>
12. <a href="#login">Fazer login no Heroku</a>
13. <a href="#projeto">Criar um novo projeto no Heroku</a>
14. <a href="#postgre">Adicionar o Banco de dados (PostgreSQL) no Heroku</a>
15. <a href="#deploy">Efetuar o Deploy</a>
16. <a href="#testar">Testar o link e a API</a>



<h2 id="swag">#Passo 01 - Criar a Documentação da API</h2>



Para criar a Documentação da API no Swagger, utilize o ebook do Swagger.



<h2 id="user">#Passo 02 - Criar o usuário em memória</h2>



Vamos criar um usuário padrão em memória para simplificar o acesso a nossa API. O usuário em memória é um usuário para testes, que dispensa o cadastro no Banco de Dados. Em produção é altamente recomendado que este usuário seja desabilitado.

Na camada Security, abra o arquivo **BasicSecurityConfig**

Vamos alterar o método **protected void configure(AuthenticationManagerBuilder auth) throws Exception** de:

```java
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        
        auth.userDetailsService(userDetailsService);
        
    }
```

Para:

```java
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		
        auth.userDetailsService(userDetailsService);
		
		auth.inMemoryAuthentication()
		.withUser("root")
		.password(passwordEncoder().encode("root"))
		.authorities("ROLE_USER");
	
	}
```



<h2 id="configure">#Passo 03 - Atualizar o método configure(HttpSecurity  http)</h2>



Vamos fazer uma atualização no método <b>configure(HttpSecurity  http)</b>, na Classe <b>BasicSecurityConfig</b>, na camada Security, para evitar erros do tipo 401 (Unauthorized) no envio de requisições via frontend no Heroku.

Na camada Security, abra o arquivo **BasicSecurityConfig** e altere o método **protected  void  configure(HttpSecurity  http) throws  Exception** de:

```java
@Override
protected  void  configure(HttpSecurity  http) throws  Exception {

http.authorizeRequests()
	.antMatchers("/usuarios/cadastrar").permitAll()
	.antMatchers("/usuarios/logar").permitAll()
	.anyRequest().authenticated()
	.and().httpBasic()
	.and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
	.and().cors()
	.and().csrf().disable();
}
```

Para:

```java
@Override
protected  void  configure(HttpSecurity  http) throws  Exception {

http.authorizeRequests()
	.antMatchers("/usuarios/cadastrar").permitAll()
	.antMatchers("/usuarios/logar").permitAll()
	.antMatchers(HttpMethod.OPTIONS).permitAll()
	.anyRequest().authenticated()
	.and().httpBasic()
    .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
	.and().cors()
	.and().csrf().disable();
}
```


O parâmetro **HttpMethod.OPTIONS** permite que o cliente (frontend), possa descobrir quais são as opções de requisição permitidas para um determinado recurso em um servidor. Nesta implementação, está sendo liberada todas as opções das requisições através do método **permitAll()**.



<h2 id="local">#Passo 04 - Testar a API no seu computador</h2>



1.Execute a sua aplicação localmente pelo Eclipse ou pelo STS

2. Abra o endereço: http://localhost:8080/ no seu navegador

4. Verifique se o Swagger abre automaticamente

6. Caso a API solicite Usuário e senha, experimente o **Usuário: root** e a **Senha: root**, que foram criados em memória.

8. Aproveite para testar todos os Endpoints da aplicação no Swagger ou no Postman (/postagens, /temas e /usuarios). 

10. Antes de continuar pare a execução do Projeto.

   

   **IMPORTANTE: Lembre-se que antes de fazer o Deploy é fundamental que a API esteja rodando e sem erros**.



<h2 id="cfhrk">#Passo 05 - Criar uma conta grátis no Heroku</h2>



1) Acesse o endereço: **https://www.heroku.com**

<div align="center"><img src="https://i.imgur.com/9lFOzru.png" title="source: imgur.com" /></div>

2) Crie a sua conta grátis no Heroku clicando no botão **SIGN UP FOR FREE** e siga as instruções.



<h2 id="node">#Passo 06 - Instalar o Node.js</h2>



1) Acesse o endereço: **https://nodejs.org/en/**

<div align="center"><img src="https://i.imgur.com/8xKcp6h.png" title="source: imgur.com" /></div>

2) Faça o download da versão 14 do Node.js e instale no seu computador.

Em caso de dúvidas, acesse o Guia de instalação do Node.js.



<h2 id="hrkcli">#Passo 07 - Instalar o Heroku Client</h2>



Para instalar e executar os comandos do Heroku Client usaremos o Prompt de comando do Windows. 

1) Para instalar, execute o atalho <img width="80" src="https://i.imgur.com/JpqKaVh.png" title="source: imgur.com" /> para abrir a janela Executar

<div align="center"><img src="https://i.imgur.com/ISBwaaK.png" title="source: imgur.com" /></div>

2) Digite o comando **cmd** para abrir o **Prompt de comando do Windows**

3) Antes de instalar o **Heroku Client**, verifique se o Node já está instalado através do comando: 

```bash
npm -version
```

<div align="justify"><img src="https://i.imgur.com/sfHThTC.png" title="source: imgur.com" /></div>

** A versão pode ser diferente da imagem*

4) Para instalar o **Heroku Client** digite o comando: 

```bash
npm i -g heroku
```

<div align="center"><img src="https://i.imgur.com/rcsDAZ0.png" title="source: imgur.com" /></div>

5) Confirme a instalação do Heroku Client através do comando: 

```bash
heroku version
```

<div align="center"><img src="https://i.imgur.com/MO23QyV.png" title="source: imgur.com" /></div>

**A versão pode ser diferente da imagem*



<h2 id="sprop">#Passo 08 - Criar o arquivo system.properties</h2>



1) Na raiz do seu projeto (em nosso exemplo, na pasta blogpessoal), crie o arquivo **system.properties**.

<div align="center"><img src="https://i.imgur.com/G70Oset.png" title="source: imgur.com" /></div>

2) No lado esquerdo superior, na Guia **Package explorer**, na pasta do projeto, clique com o botão direito do mouse e clique na opção **New->File**.

3) Em **File name**, digite o nome do arquivo (**system.properties**) e clique no botão **Finish**.

<div align="center"><img src="https://i.imgur.com/gffhZoF.png" title="source: imgur.com" /></div>

4) No arquivo **system.properties** indique a versão do Java que será utilizada pela API no Heroku:

```properties
java.runtime.version=16
```



<h2 id="pom02">#Passo 09 - Configurar o PostgreSQL no arquivo pom.xml</h2>


No arquivo, **pom.xml**, vamos inserir as linhas abaixo, com a dependência do PostgreSQL:

```xml
<dependency>
	<groupId>org.postgresql</groupId>
	<artifactId>postgresql</artifactId>
</dependency> 
```



<h2 id="approp">#Passo 10 - Configuração do Banco de Dados no arquivo application.properties</h2>


A Configuração do Banco de dados Local é diferente da configuração que será utilizada no Heroku. Para simplificar o processo, vamos utilizar o conceito de profiles (perfis), desta forma será possível alternar entre as configurações Local (MySQL) e Remota (PostgreSQL) de forma simples e rápida.

1) Na Source Folder **src/main/resources**, crie os arquivos **application-dev.properties** (Configuração do Banco de dados local) e **application-prod.properties** (Configuração do Banco de dados na nuvem).

<div align="center"><img src="https://i.imgur.com/BwhjMda.png" title="source: imgur.com" width="230px"/></div>

2) No lado esquerdo superior, na Guia **Package explorer**, na Source Folder **src/main/resources**, clique com o botão direito do mouse e clique na opção **New 🡢 File**.

3) Em **File name**, digite o nome do primeiro arquivo (**application-dev.properties**) e clique no botão **Finish**.

<div align="center"><img src="https://i.imgur.com/e9PVjRL.png" title="source: imgur.com" /></div>

4) Repita os itens 2 e 3 para criar o segundo arquivo **application-prod.properties**.

Agora vamos configurar os 3 arquivos:

1. Abra o arquivo **application.properties**, selecione todo o seu conteúdo e Recorte **(Ctrl + X)**.

2. Abra o arquivo **application-dev.properties**, Cole **(Ctrl + V)** o conteúdo recortado do arquivo **application.properties** e salve o arquivo.

3. O arquivo  **application-dev.properties** ficará com a configuração semelhante a esta:

```properties
spring.jpa.hibernate.ddl-auto=update
spring.jpa.database=mysql
spring.datasource.url=jdbc:mysql://localhost/db_blogpessoal?createDatabaseIfNotExist=true&serverTimezone=America/Sao_Paulo&useSSl=false
spring.datasource.username=root
spring.datasource.password=root
spring.jpa.properties.hibernate.dialect = org.hibernate.dialect.MySQL8Dialect

spring.jpa.show-sql=true

spring.jackson.date-format=yyyy-MM-dd HH:mm:ss
spring.jackson.time-zone=Brazil/East
```

4. No arquivo, **application.properties**, vamos inserir o conteúdo abaixo:

```properties
spring.profiles.active=prod
```

5. Para alternar entre as configurações, utilizaremos as 2 opções abaixo:

<b><code>spring.profiles.active=dev</code> </b> 🡢 O Spring executará a aplicação com a configuração do Banco de dados local (MySQL)

<b><code>spring.profiles.active=prod</code> </b> 🡢 O Spring executará a aplicação com a configuração do Banco de dados na nuvem (Heroku)

Para o Deploy, devemos deixar a linha **spring.profiles.active** configurada com a opção **prod**.

6. No arquivo, **application-prod.properties**, vamos inserir o conteúdo **exatamente** como está abaixo:

```properties
spring.jpa.generate-ddl=true
spring.datasource.url=${JDBC_DATASOURCE_URL}
spring.jpa.show-sql=true

spring.jackson.date-format=yyyy-MM-dd HH:mm:ss
spring.jackson.time-zone=Brazil/East
```
<br />
<div align="center"> <h1>*** Importante ***</h1></div>

<div align="justify">A partir deste ponto, <b>o seu projeto não executará mais localmente (http://localhost:8080/)</b>. Para voltar a executar localmente, <b>abra o arquivo application.properties e altere a 1º linha de <i>prod</i> para <i>dev</i></b>.  
</div>





<h2 id="git"># Passo 11 - Deploy com o Git</h2>



Vamos preparar o nosso repositório local para subir a aplicação para o Heroku utilizando o Git.

1. Na pasta do projeto, clique com o botão direito do mouse e na sequência clique na opção: **Show in 🡢 System Explorer**

<div align="center"><img src="https://i.imgur.com/ZgiW14F.png" title="source: imgur.com" /></div>

2. Será aberta a pasta Workspace onde o Eclipse/STS grava os seus projetos: 

-   Se você estiver usando o STS geralmente a pasta fica em: **c:\Usuarios\seu usuario\Documents\workspace-spring-tool-suite-4-4.11.0.RELEASE** (a versão pode ser diferente).    
   
    _*seu usuario = Usuário do seu computador_
    

3. Abra esta pasta do projeto e verifique se existe uma pasta chamada **.git**. Caso ela exista, apague esta pasta. **Esta pasta estará presente <u>APENAS</u> se você inicializou o git dentro da pasta do projeto.**
   

<div align="left"><img src="https://i.imgur.com/2vzoKD4.png" title="source: imgur.com" /></div>

Caso esta pasta não esteja sendo exibida, na janela do Windows Explorer, clique na **Guia Exibir** e na sequência no botão **Opções**. Na janela **Opções de Pasta**, na **Guia Modo de Exibição**, no item **Configurações avançadas**, localize a opção: **Pastas e arquivos ocultos** e marque a opção **Mostrar arquivos, pastas e unidades ocultas** (como mostra a figura abaixo). Em seguida clique em **OK** para concluir.

<div align="center"><img width="340px" src="https://i.imgur.com/n8hQu12.png" title="source: imgur.com" /></div>

4. Execute o atalho <img width="80" src="https://i.imgur.com/JpqKaVh.png" title="source: imgur.com" /> para abrir a janela Executar

<div align="center"><img src="https://i.imgur.com/ISBwaaK.png" title="source: imgur.com" /></div>

5. Digite o comando abaixo para abrir o **Prompt de Comando do Windows**:

```
cmd
```
6. Na pasta do seu projeto, no **Windows Explorer**, copie o caminho da pasta conforme a figura abaixo:

<div align="center"><img src="https://i.imgur.com/yI6at9T.png" title="source: imgur.com" /></div>

7. No Prompt de comando do Windows digite o comando cd e cole na frente do comando o caminho copiado: 

```
cd C:\Users\seu usuario\Documents\
workspace-spring-tool-suite-4-4.11.0.RELEASE\deploy_blogpessoal
```
**o nome da pasta pode ser diferente*

8. Digite a sequência de comandos abaixo para inicializar o seu repositório local para efetuar o Deploy no Heroku:

```
git init
git add .
git commit -m “Deploy inicial - Blog Pessoal”
```
<br />

<h2 id="login">#Passo 12 - Login no Heroku</h2>



1- Digite o comando: 

```
heroku login
```
<div><img src="https://i.imgur.com/pvygxsZ.png" title="source: imgur.com" /></div>

2- Será aberta a janela abaixo. Clique no botão **Log in**

<div align="center"><img src="https://i.imgur.com/PXR6hFW.png" title="source: imgur.com" /></div>

3- Após efetuar o login na sua conta, será exibida a janela abaixo. 

<div align="center"><img src="https://i.imgur.com/i6VMoMp.png" title="source: imgur.com" /></div>

4- Volte para o Prompt de comando para continuar o Deploy.

<div align="center"><img src="https://i.imgur.com/IjyMzrH.png" title="source: imgur.com" /></div>



<h2 id="projeto">#Passo 13 - Criar um novo projeto no Heroku</h2>



Para criar um novo projeto na sua conta do Heroku, digite o comando:

```
heroku create nomedoprojeto
```

<div align="center"> <h1>*** Importante ***</h1></div>
**O NOME DO PROJETO NÃO PODE TER LETRAS MAIUSCULAS, NUMEROS OU CARACTERES ESPECIAIS. ALÉM DISSO ELE PRECISA SER UNICO DENTRO DA PLATAFORMA HEROKU.**

Se o nome escolhido já existir, será exibida a mensagem abaixo:

<div><img src="https://i.imgur.com/L7ayFaz.png" title="source: imgur.com" /></div>

Se o nome escolhido for aceito, será exibida a mensagem abaixo:

<div><img src="https://i.imgur.com/P0KazWd.png" title="source: imgur.com" /></div>



<h2 id="postgre">#Passo 14 - Adicionar o Banco de dados (PostgreSQL) no Heroku</h2>



Para adicionar um Banco de Dados PostgreSQL no seu projeto, digite o comando:

```bash
heroku addons:create heroku-postgresql:hobby-dev -a nomedoprojeto
```

<div><img src="https://i.imgur.com/edhMr8x.png" title="source: imgur.com" /></div>



<h2 id="deploy">#Passo 15 - Efetuar o Deploy</h2>



Para concluir o Deploy, digite o comando: 

```
git push heroku master
```

Se tudo deu certo, será exibida a mensagem **BUILD SUCESS** (destacado em verde na imagem) e será exibido o endereço (**https://nomedoprojeto.herokuapp.com**) para acessar a API na Internet (destacado em amarelo na imagem)

<div align="center"><img src="https://i.imgur.com/JMyNMLx.png" title="source: imgur.com" /></div>



<h2 id="testar">#Passo 16 - Testar o link e a API</h2>



1) Abra o navegador e digite o endereço a sua API

2) Será solicitado o usuário e a senha. Digite **root** para ambos.

3) Sua API abrirá o Swagger. 

<div align="center"><img src="https://i.imgur.com/WWG2GJw.png" title="source: imgur.com" /></div>

4) Faça alguns testes via Swagger para certificar-se de que tudo está funcionando



<h2 id="update">Atualizar o Deploy no Heroku </h2>



Uma vez que o Deploy foi feito no Heroku, assim como no Github, basta atualizar os arquivos na pasta **deploy_blogpessoal** e efetuar a sequência de comandos abaixo para atualizar o Deploy.

```
git add .
git commit -m “Atualização do Deploy - Blog Pessoal”
git push heroku master
```

Caso ocorra algum erro de vinculação (link), verifique se a pasta está vinculada ao Heroku utilizando o comando abaixo:

```
git remote
```

Caso não apareça o resultado heroku, utilize o comando abaixo para vincular a pasta com o heroku.

```
heroku git:remote -a project
```
Caso o comando acima falhe, inicialize o repositório git e refaça a vinculação.
```
git init
heroku git:remote -a project
```
Para atualizar o Deploy, utilize os comandos baixo:
```
git add .
git commit -m “Atualização do Deploy - Blog Pessoal”
git push heroku master
```
Caso o ultimo comando falhe, acrescente a opção -f para forçar o Deploy.
```
git push -f heroku master
```
Se todas as opções acima falharem, verifique se o erro não está na aplicação.
