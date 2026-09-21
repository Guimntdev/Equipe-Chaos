package pokesal.batalha;

import pokesal.excecao.InicialInvalidoException;
import pokesal.modelo.Pokesal;
import pokesal.modelo.TipoElemental;

/**
 * Catálogo dos seis iniciais permitidos no torneio.
 */
public final class CatalogoInicial {

  private static final String[] NOMES_PERMITIDOS = {
      "BulbaSal", "CharSal", "SquirtSal", "ChikoSal", "CyndaSal", "TotoSal"
  };

  private CatalogoInicial() {
  }

  /**
   * Nomes válidos para escolha do inicial.
   *
   * @return cópia da lista de nomes
   */
  public static String[] nomesPermitidos() {
    String[] copia = new String[NOMES_PERMITIDOS.length];
    for (int i = 0; i < NOMES_PERMITIDOS.length; i++) {
      copia[i] = NOMES_PERMITIDOS[i];
    }
    return copia;
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
    if (nome.equals("BulbaSal")) {
      return new Pokesal("BulbaSal", TipoElemental.PLANTA, 45, 49, 49, 45);
    }
    if (nome.equals("CharSal")) {
      return new Pokesal("CharSal", TipoElemental.FOGO, 39, 52, 43, 65);
    }
    if (nome.equals("SquirtSal")) {
      return new Pokesal("SquirtSal", TipoElemental.AGUA, 44, 48, 65, 43);
    }
    if (nome.equals("ChikoSal")) {
      return new Pokesal("ChikoSal", TipoElemental.PLANTA, 50, 45, 55, 40);
    }
    if (nome.equals("CyndaSal")) {
      return new Pokesal("CyndaSal", TipoElemental.FOGO, 40, 55, 40, 60);
    }
    if (nome.equals("TotoSal")) {
      return new Pokesal("TotoSal", TipoElemental.AGUA, 50, 50, 48, 43);
    }
    throw new InicialInvalidoException(
        "Inicial invalido: " + nome + ". Use um dos seis permitidos.");
  }
}
