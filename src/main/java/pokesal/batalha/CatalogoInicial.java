package pokesal.batalha;

import java.util.Arrays;
import pokesal.excecao.InicialInvalidoException;
import pokesal.modelo.Pokesal;
import pokesal.modelo.TipoElemental;

/**
 * Catálogo dos seis iniciais permitidos no torneio.
 */
public final class CatalogoInicial {

  private static final String BULBASAL = "BulbaSal";
  private static final String CHARSAL = "CharSal";
  private static final String SQUIRTSAL = "SquirtSal";
  private static final String CHIKOSAL = "ChikoSal";
  private static final String CYNDASAL = "CyndaSal";
  private static final String TOTOSAL = "TotoSal";

  private static final String[] NOMES_PERMITIDOS = {
      BULBASAL, CHARSAL, SQUIRTSAL, CHIKOSAL, CYNDASAL, TOTOSAL
  };

  private CatalogoInicial() {
  }

  /**
   * Nomes válidos para escolha do inicial.
   *
   * @return cópia da lista de nomes
   */
  public static String[] nomesPermitidos() {
    return Arrays.copyOf(NOMES_PERMITIDOS, NOMES_PERMITIDOS.length);
  }

  /**
   * Cria uma instância nova do inicial escolhido.
   *
   * @param nome nome exato do Pokésal
   * @return inicial pronto para batalha
   * @throws InicialInvalidoException se o nome não estiver na lista
   */
  public static Pokesal criar(String nome) {
    if (nome == null) {
      throw new InicialInvalidoException("Nome do inicial nao pode ser nulo.");
    }
    if (nome.equals(BULBASAL)) {
      return new Pokesal(BULBASAL, TipoElemental.PLANTA, 45, 49, 49, 45);
    }
    if (nome.equals(CHARSAL)) {
      return new Pokesal(CHARSAL, TipoElemental.FOGO, 39, 52, 43, 65);
    }
    if (nome.equals(SQUIRTSAL)) {
      return new Pokesal(SQUIRTSAL, TipoElemental.AGUA, 44, 48, 65, 43);
    }
    if (nome.equals(CHIKOSAL)) {
      return new Pokesal(CHIKOSAL, TipoElemental.PLANTA, 50, 45, 55, 40);
    }
    if (nome.equals(CYNDASAL)) {
      return new Pokesal(CYNDASAL, TipoElemental.FOGO, 40, 55, 40, 60);
    }
    if (nome.equals(TOTOSAL)) {
      return new Pokesal(TOTOSAL, TipoElemental.AGUA, 50, 50, 48, 43);
    }
    throw new InicialInvalidoException(
        "Inicial invalido: " + nome + ". Use um dos seis permitidos.");
  }
}
