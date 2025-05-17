public class Data {
    // Número de dias em cada mês para um ano não bissexto
    private static final int[] diasNoMes = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    public Data(){}

    /*========================================================================
    diasAteData: Calcula o número de dias desde uma data marco (1/1/1800) até 
                 uma data fornecida.
     =========================================================================*/

    public int diasAteData(int dd, int mm, int aaaa) {
        // Data marco fixada como exemplo (1 de janeiro de 1800)
        int anoMarco = 1800;
        int mesMarco = 1;
        int diaMarco = 1;

        // Calcular a quantidade total de dias desde 1/1/0000 até a data marco
        int diasMarco = calcularDiasDesdeMarco(diaMarco, mesMarco, anoMarco);
        // Calcular a quantidade total de dias desde 1/1/0000 até a data fornecida
        int diasData = calcularDiasDesdeMarco(dd, mm, aaaa);

        return diasData - diasMarco;
    }

    /*========================================================================
    calcularDiasDesdeMarco: Calcula o número total de dia até uma data específica.
     =========================================================================*/

    private int calcularDiasDesdeMarco(int dia, int mes, int ano) {
        int dias = 0;

        // Adicionar dias dos anos completos
        for (int a = 0; a < ano; a++) {
            dias += (isBissexto(a)) ? 366 : 365;
        }

        // Adicionar dias dos meses do ano corrente
        for (int m = 1; m < mes; m++) {
            dias += diasNoMes(m, ano);
        }

        // Adicionar dias dos dias do mês corrente
        dias += dia - 1;

        return dias;
    }

    /*========================================================================
    diasNoMes: Retorna o número de dias de um mês específico, considerando 
               anos bissextos.
     =========================================================================*/

    private int diasNoMes(int mes, int ano) {
        if (mes == 2 && isBissexto(ano)) {
            return 29;
        }
        return diasNoMes[mes - 1];
    }

    //Verifica se um ano é bissexto
    private boolean isBissexto(int ano) {
        return (ano % 4 == 0 && ano % 100 != 0) || (ano % 400 == 0);
    }

    //Converte uma quantidade de dias desde a data marco (1 de janeiro de 1900) para uma data no formato dd/MM/yy.
    public String diasParaData(long dias) {
        // Data marco fixada como exemplo (1 de janeiro de 1800)
        int ano = 1800;
        int mes = 1;
        int dia = 1;

        // Adicionar dias desde a data marco
        while (dias > 0) {
            int diasNoMes = diasNoMes(mes, ano);
            if (dias + dia > diasNoMes) {
                dias -= (diasNoMes - dia + 1);
                dia = 1;
                mes++;
                if (mes > 12) {
                    mes = 1;
                    ano++;
                }
            } else {
                dia += dias;
                dias = 0;
            }
        }

        return String.format("%02d/%02d/%04d", dia, mes, ano);
    }
}
