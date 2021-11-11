package br.org.generation.blogpessoal.service;

import java.nio.charset.Charset;
import java.util.Optional;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import br.org.generation.blogpessoal.model.Usuario;
import br.org.generation.blogpessoal.model.UsuarioLogin;
import br.org.generation.blogpessoal.repository.UsuarioRepository;

/**
 *  A Classe UsuarioService implementa as regras de negócio do Usuario.
 *  
 *  Regras de negócio são as particularidades das funcionalidades a serem 
 *  implementadas no objeto, tais como:
 *  
 *  1) O Usuário não pode estar duplicado no Banco de dados
 *  2) O Usuario deve ser maior de 18 anos
 *  
 *  Observe que toda a implementação dos metodos Cadastrar, Atualizar e 
 *  Logar estão implmentadas na classe de serviço, enquanto a Classe
 *  Controller se limitará a checar se deu certo ou errado a requisição.
 */
/**
* A annotation @Service indica que esta é uma Classe de Serviço, ou seja,
* implementa regras de negócio da aplicação
*/

@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;

	public Optional<Usuario> cadastrarUsuario(Usuario usuario) {
		/**
		 *  Checa se o usuário já existe no Banco de Dados. 
		 *  Se não existir retorna vazio
		 *  
		 *  isPresent() -> Se um valor estiver presente retorna true, caso contrário
		 *  retorna vazio.
		 */

		if (usuarioRepository.findByUsuario(usuario.getUsuario()).isPresent())
			return Optional.empty();

		usuario.setSenha(criptografarSenha(usuario.getSenha()));
		/**
		 * Retorna para a Classe UsuarioController o objeto Salvo no Banco de Dados
		 * A Classe controladora checará se deu tudo certo nesta operação
		 * 
		 * Optional.of -> Retorna um Optional com o valor fornecido, mas o valor não 
		 * pode ser nulo. Como nosso método possui um Optional na sua assinatura, 
		 * o retorno também deve ser um Optional.
		 */
		return Optional.of(usuarioRepository.save(usuario));

	}
	
	

	public Optional<Usuario> atualizarUsuario(Usuario usuario) {
		/**
		 * Verifica através no metodo findById se o id para atualização existe no banco
		 * Se o usuario isPresent vamos verificar se o ID enviado é diferente 
		 * do usuario informado, se for retorna vazio
		 * Se não criptografa a senha e salva os dados 
		 * Se o id não estver presente ele retorna vazio
		 */
		if (usuarioRepository.findById(usuario.getId()).isPresent()) {

			Optional<Usuario> buscaUsuario = usuarioRepository.findByUsuario(usuario.getUsuario());

			if (buscaUsuario.isPresent()) {
				if (buscaUsuario.get().getId() != usuario.getId())
					return Optional.empty();
			}

			usuario.setSenha(criptografarSenha(usuario.getSenha()));

			return Optional.of(usuarioRepository.save(usuario));
		}

		return Optional.empty();
	}
	/**
	 *  A principal função do método autenticarUsuario, que é executado no endpoint logar,
	 *  é gerar o token do usuário codificado em Base64. O login prorpiamente dito é executado
	 *  pela BasicSecurityConfig em conjunto com as classes UserDetailsService e Userdetails
	 */
	public Optional<UsuarioLogin> autenticarUsuario(Optional<UsuarioLogin> usuarioLogin) {

		Optional<Usuario> usuario = usuarioRepository.findByUsuario(usuarioLogin.get().getUsuario());

		if (usuario.isPresent()) {
		
			if (compararSenhas(usuarioLogin.get().getSenha(), usuario.get().getSenha())) {

				usuarioLogin.get().setId(usuario.get().getId());
				usuarioLogin.get().setNome(usuario.get().getNome());
				usuarioLogin.get().setSenha(usuario.get().getSenha());
				usuarioLogin.get()
						.setToken(gerarBasicToken(usuarioLogin.get().getUsuario(), usuarioLogin.get().getSenha()));

				return usuarioLogin;

			}
		}

		return Optional.empty();

	}

	private String criptografarSenha(String senha) {
		/**
		 *  Instancia um objeto da Classe BCryptPasswordEncoder para criptografar
		 *  a senha
		 */
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

		return encoder.encode(senha);

	}

	private boolean compararSenhas(String senhaDigitada, String senhaBanco) {

	
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

		return encoder.matches(senhaDigitada, senhaBanco);

	}

	private String gerarBasicToken(String email, String password) {

		String tokenBase = email + ":" + password;
		byte[] tokenBase64 = Base64.encodeBase64(tokenBase.getBytes(Charset.forName("US-ASCII")));
		return "Basic " + new String(tokenBase64);

	}

}