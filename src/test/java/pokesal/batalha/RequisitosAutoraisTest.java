package pokesal.batalha;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import pokesal.excecao.InicialInvalidoException;
import pokesal.modelo.Pokesal;
import pokesal.modelo.StatusEfeito;
import pokesal.modelo.Terreno;
import pokesal.modelo.TipoElemental;
import pokesal.modelo.TipoGolpe;
import pokesal.modelo.Treinador;
import org.junit.jupiter.api.Test;

/**
 * Testes dos requisitos autorais da equipe.
 */
class RequisitosAutoraisTest {

  @Test
  void testComboGolpeConsecutivo() {
    Pokesal atacante = new Pokesal("Atk", TipoElemental.FOGO, 200, 50, 50, 60);
    Pokesal defensor = new Pokesal("Def", TipoElemental.FOGO, 200, 50, 50, 40);
    Treinador ana = new Treinador("Ana", atacante);
    Treinador beto = new Treinador("Beto", defensor);
    Batalha batalha = new Batalha(ana, beto, Terreno.CANTEIRO_CENTRAL);

    int primeiro = batalha.executarAtaque(ana, beto, TipoGolpe.ELEMENTAL);
    int segundo = batalha.executarAtaque(ana, beto, TipoGolpe.ELEMENTAL);
    int esperado = (int) Math.round(
        primeiro * (1.0 + ConstantesBatalha.BONUS_COMBO));

    assertTrue(segundo > primeiro);
    assertEquals(esperado, segundo);
  }

  @Test
  void testComboResetaComGolpeDiferente() {
    Pokesal atacante = new Pokesal("Atk", TipoElemental.FOGO, 200, 50, 50, 60);
    Pokesal defensor = new Pokesal("Def", TipoElemental.FOGO, 200, 50, 50, 40);
    Treinador ana = new Treinador("Ana", atacante);
    Treinador beto = new Treinador("Beto", defensor);
    Batalha batalha = new Batalha(ana, beto, Terreno.CANTEIRO_CENTRAL);

    int elemental = batalha.executarAtaque(ana, beto, TipoGolpe.ELEMENTAL);
    batalha.executarAtaque(ana, beto, TipoGolpe.INVESTIDA);
    int deNovo = batalha.executarAtaque(ana, beto, TipoGolpe.ELEMENTAL);

    assertEquals(elemental, deNovo);
  }

  @Test
  void testLutarAteOFimNaoEmpata() {
    Pokesal a = new Pokesal("A", TipoElemental.FOGO, 10, 40, 40, 50);
    Pokesal b = new Pokesal("B", TipoElemental.FOGO, 10, 40, 40, 40);
    a.aplicarStatus(StatusEfeito.QUEIMADO);
    b.aplicarStatus(StatusEfeito.QUEIMADO);
    Treinador t1 = new Treinador("Ana", a);
    Treinador t2 = new Treinador("Beto", b);
    Batalha batalha = new Batalha(t1, t2, Terreno.CANTEIRO_CENTRAL);

    int turnos = 0;
    while (!batalha.batalhaEncerrada() && turnos < 50) {
      batalha.finalizarTurno();
      turnos++;
    }

    assertTrue(batalha.batalhaEncerrada());
    Treinador vencedor = batalha.obterVencedor();
    assertNotNull(vencedor, "Batalha nao pode terminar empatada.");
  }

  @Test
  void testSemLimiteDeTurnos() {
    Pokesal a = new Pokesal("A", TipoElemental.FOGO, 80, 10, 80, 50);
    Pokesal b = new Pokesal("B", TipoElemental.FOGO, 80, 10, 80, 40);
    Treinador t1 = new Treinador("Ana", a);
    Treinador t2 = new Treinador("Beto", b);
    Batalha batalha = new Batalha(t1, t2, Terreno.CANTEIRO_CENTRAL);

    int i = 0;
    while (i < 15) {
      batalha.finalizarTurno();
      i++;
    }

    assertTrue(batalha.getNumeroTurno() > 10);
    assertTrue(!batalha.batalhaEncerrada());
  }

  @Test
  void testStatusNaoAcumulaSubstituiAnterior() {
    Pokesal pokesal = CatalogoInicial.criar("TotoSal");
    pokesal.aplicarStatus(StatusEfeito.QUEIMADO);
    assertEquals(StatusEfeito.QUEIMADO, pokesal.getStatus());

    pokesal.aplicarStatus(StatusEfeito.ENVENENADO);
    assertEquals(StatusEfeito.ENVENENADO, pokesal.getStatus());
    assertEquals(0, pokesal.getTurnosEnvenenado());

    pokesal.aplicarStatus(StatusEfeito.PARALISADO);
    assertEquals(StatusEfeito.PARALISADO, pokesal.getStatus());
  }

  @Test
  void testInicialInvalidoNaoEntraNaBatalha() {
    assertThrows(InicialInvalidoException.class, () -> {
      CatalogoInicial.criar("Pikachu");
    });
  }

  @Test
  void testQueimadoReduzAtaqueETiraHp() {
    Pokesal charSal = CatalogoInicial.criar("CharSal");
    int atkAntes = charSal.getAtkEfetivo();
    charSal.aplicarStatus(StatusEfeito.QUEIMADO);
    int atkQueimado = (int) Math.round(
        atkAntes * (1.0 - ConstantesBatalha.REDUCAO_ATK_QUEIMADO));
    assertEquals(atkQueimado, charSal.getAtkEfetivo());

    int hpAntes = charSal.getHpAtual();
    charSal.aplicarEfeitosStatusFimTurno();
    assertTrue(charSal.getHpAtual() < hpAntes);
  }

  @Test
  void testParalisiaReduzVelocidade() {
    Pokesal charSal = CatalogoInicial.criar("CharSal");
    Pokesal squirtSal = CatalogoInicial.criar("SquirtSal");
    Treinador ana = new Treinador("Ana", charSal);
    Treinador beto = new Treinador("Beto", squirtSal);
    Batalha batalha = new Batalha(ana, beto, Terreno.ASFALTO_QUENTE);

    assertEquals(ana, batalha.obterPrimeiroAAgir());
    charSal.aplicarStatus(StatusEfeito.PARALISADO);
    assertTrue(charSal.getSpdEfetivo() < charSal.getSpdBase());
    assertEquals(beto, batalha.obterPrimeiroAAgir());
  }
}
