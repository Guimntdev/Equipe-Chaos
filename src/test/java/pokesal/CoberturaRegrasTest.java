package pokesal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import pokesal.batalha.Batalha;
import pokesal.batalha.CalculadoraDano;
import pokesal.batalha.CatalogoInicial;
import pokesal.excecao.InicialInvalidoException;
import pokesal.modelo.AcaoTurno;
import pokesal.modelo.Pokesal;
import pokesal.modelo.StatusEfeito;
import pokesal.modelo.Terreno;
import pokesal.modelo.TipoElemental;
import pokesal.modelo.TipoGolpe;
import pokesal.modelo.TipoItem;
import pokesal.modelo.Treinador;

/**
 * Casos extras para cobrir catálogo, itens e golpes que os testes
 * obrigatórios não passam.
 */
class CoberturaRegrasTest {

  @Test
  void catalogoSoAceitaOsSeisIniciais() {
    String[] nomes = CatalogoInicial.nomesPermitidos();
    assertEquals(6, nomes.length);
    for (int i = 0; i < nomes.length; i++) {
      assertNotNull(CatalogoInicial.criar(nomes[i]));
    }
    assertThrows(InicialInvalidoException.class,
        () -> CatalogoInicial.criar("Pikachu"));
    assertThrows(InicialInvalidoException.class,
        () -> CatalogoInicial.criar(null));
  }

  @Test
  void potionAntidoteEInvestida() {
    Treinador t1 = new Treinador("Miguel", CatalogoInicial.criar("CharSal"));
    Treinador t2 = new Treinador("Dantton", CatalogoInicial.criar("BulbaSal"));
    t1.getPokesal().receberDano(25);
    int hpFerido = t1.getPokesal().getHpAtual();
    t1.usarItem(TipoItem.SUPER_POTION);
    assertTrue(t1.getPokesal().getHpAtual() > hpFerido);

    t2.getPokesal().aplicarStatus(StatusEfeito.ENVENENADO);
    t2.usarItem(TipoItem.ANTIDOTE);
    assertEquals(StatusEfeito.NENHUM, t2.getPokesal().getStatus());

    Batalha batalha = new Batalha(t1, t2, Terreno.ASFALTO_QUENTE);
    int danoInvestida = batalha.executarAtaque(t1, t2, TipoGolpe.INVESTIDA);
    CalculadoraDano calc = new CalculadoraDano();
    int danoElemental = calc.calcular(t1.getPokesal(), t2.getPokesal(),
        Terreno.ASFALTO_QUENTE, TipoGolpe.ELEMENTAL, false);
    assertTrue(danoInvestida < danoElemental);
  }

  @Test
  void turnoEncerraSeAlguemCairNoGolpe() {
    Treinador t1 = new Treinador("Miguel",
        new Pokesal("A", TipoElemental.FOGO, 80, 80, 10, 80));
    Treinador t2 = new Treinador("Dantton",
        new Pokesal("B", TipoElemental.PLANTA, 5, 10, 10, 10));
    Batalha batalha = new Batalha(t1, t2, Terreno.ASFALTO_QUENTE);
    batalha.resolverTurno(AcaoTurno.ATACAR, null, AcaoTurno.ATACAR, null);
    assertTrue(batalha.batalhaEncerrada());
    assertEquals(t1, batalha.obterVencedor());
  }
}
