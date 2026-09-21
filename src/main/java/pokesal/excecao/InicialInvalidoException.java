package pokesal.excecao;

/**
 * Lançada quando o nome do inicial não está na lista permitida.
 */
public class InicialInvalidoException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  /**
   * Cria a exceção com a mensagem informada.
   *
   * @param mensagem detalhe do erro
   */
  public InicialInvalidoException(String mensagem) {
    super(mensagem);
  }
}
