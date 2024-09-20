package com.mycompany.operaciones;
import java.util.*;

public class Operaciones {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Ingrese la expresión matemática en el formato <expresión> (o 'salir' para terminar):");

        while (true) {
            String expresion = scanner.nextLine();
            if (expresion.equalsIgnoreCase("salir")) {
                break;
            }

            if (!esExpresionValida(expresion)) {
                System.out.println("La expresión matemática debe estar en el formato <expresión>. Inténtelo de nuevo.");
                continue; // Volver a solicitar una nueva expresión
            }

            // Quitar los signos de menor y mayor antes de evaluar
            String expresionSinMarcadores = expresion.substring(1, expresion.length() - 1);

            try {
                double resultado = evaluar(expresionSinMarcadores);
                // Convertir a entero y mostrar el resultado sin decimales
                System.out.println("Resultado de la expresión \"" + expresion + "\": " + (int) resultado);
                break; // Terminar el programa después de mostrar el resultado
            } catch (Exception e) {
                System.out.println("Error al evaluar la expresión \"" + expresion + "\": " + e.getMessage());
            }
        }

        scanner.close();
    }

    private static boolean esExpresionValida(String expresion) {
        // Verificar que la expresión comience con '<' y termine con '>'
        return expresion.startsWith("<") && expresion.endsWith(">") && expresion.length() > 2;
    }

    private static double evaluar(String expresion) throws Exception {
        List<String> tokens = tokenize(expresion);
        List<String> posfija = infijaAPosfija(tokens);
        return evaluaPosfija(posfija);
    }

    private static List<String> tokenize(String expresion) {
        List<String> tokens = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();
        
        for (char c : expresion.toCharArray()) {
            if (Character.isDigit(c) || c == '.') {
                currentToken.append(c);
            } else {
                if (currentToken.length() > 0) {
                    tokens.add(currentToken.toString());
                    currentToken.setLength(0);
                }
                if (!Character.isWhitespace(c)) {
                    tokens.add(String.valueOf(c));
                }
            }
        }
        
        if (currentToken.length() > 0) {
            tokens.add(currentToken.toString());
        }
        
        return tokens;
    }

    private static List<String> infijaAPosfija(List<String> tokens) throws Exception {
        List<String> salida = new ArrayList<>();
        Stack<String> operadores = new Stack<>();

        Map<String, Integer> precedencia = new HashMap<>();
        precedencia.put("+", 1);
        precedencia.put("-", 1);
        precedencia.put("*", 2);
        precedencia.put("/", 2);
        precedencia.put("^", 3);
        precedencia.put("sqrt", 4);
        precedencia.put("(", 0);

        for (String token : tokens) {
            if (isNumero(token)) {
                salida.add(token);
            } else if (token.equals("(")) {
                operadores.push(token);
            } else if (token.equals(")")) {
                while (!operadores.isEmpty() && !operadores.peek().equals("(")) {
                    salida.add(operadores.pop());
                }
                operadores.pop(); 
            } else {
                while (!operadores.isEmpty() && precedencia.get(token) <= precedencia.get(operadores.peek())) {
                    salida.add(operadores.pop());
                }
                operadores.push(token);
            }
        }

        while (!operadores.isEmpty()) {
            salida.add(operadores.pop());
        }

        return salida;
    }

    private static double evaluaPosfija(List<String> posfija) throws Exception {
        Stack<Double> stack = new Stack<>();

        for (String token : posfija) {
            if (isNumero(token)) {
                stack.push(Double.parseDouble(token));
            } else {
                double resultado;
                if (token.equals("sqrt")) {
                    double b = stack.pop();
                    resultado = Math.sqrt(b);
                } else {
                    double b = stack.pop();
                    double a = stack.pop();
                    resultado = aplicarOperacion(a, b, token);
                }
                stack.push(resultado);
            }
        }

        return stack.pop();
    }

    private static boolean isNumero(String token) {
        try {
            Double.parseDouble(token);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static double aplicarOperacion(double a, double b, String operador) throws Exception {
        switch (operador) {
            case "+":
                return a + b;
            case "-":
                return a - b;
            case "*":
                return a * b;
            case "/":
                if (b == 0) {
                    throw new Exception("División por cero");
                }
                return a / b;
            case "^":
                return Math.pow(a, b);
            default:
                throw new Exception("Operador desconocido: " + operador);
        }
    }
}
