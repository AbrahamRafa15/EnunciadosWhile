import javax.swing.JFileChooser;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BooleanExpressionParser {

    // =========================
    //          MAIN
    // =========================
    public static void main(String[] args) {
        // 1. Abrimos diálogo de selección de archivo
        String filePath = openFileDialog();
        if (filePath == null) {
            System.out.println("No se seleccionó ningún archivo. Saliendo.");
            return;
        }

        // 2. Leemos el contenido del archivo
        String input;
        try {
            input = Files.readString(Paths.get(filePath));
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
            return;
        }

        // 3. Analizamos léxicamente el contenido para obtener tokens
        List<Token> tokens = LexicalAnalyzer.tokenize(input);

        // 4. Analizamos sintácticamente (parse) las sentencias
        Parser parser = new Parser(tokens);
        try {
            parser.parseAllStatements();
            System.out.println("¡El archivo cumple con la(s) sentencia(s) esperada(s)!");
            generarEstadisticas(tokens);
        } catch (RuntimeException e) {
            System.out.println("La expresión a parsear es incorrecta.");
        }
    }

    /**
     * Genera y muestra estadísticas sobre:
     * - Número total de variables diferentes (una sola letra).
     * - Número total de constantes (un solo dígito).
     * - Número total de operadores de comparación.
     * - Número total de while.
     */
    private static void generarEstadisticas(List<Token> tokens) {
        Set<String> variablesDiferentes = new HashSet<>();
        int constantesUnDigito = 0;
        int operadoresComparacion = 0;
        int whileCount = 0;

        for (Token t : tokens) {
            switch (t.type()) {
                case IDENTIFIER -> {
                    String nombre = t.text();
                    if (nombre.length() == 1 && Character.isLetter(nombre.charAt(0))) {
                        variablesDiferentes.add(nombre);
                    }
                }
                case NUMBER -> {
                    String numero = t.text();
                    if (numero.length() == 1 && Character.isDigit(numero.charAt(0))) {
                        constantesUnDigito++;
                    }
                }
                case EQ, NE, LT, LE, GT, GE -> operadoresComparacion++;
                case WHILE -> whileCount++;
                default -> {
                    // No se hace nada con otros tokens.
                }
            }
        }

        System.out.println("===== ESTADÍSTICAS =====");
        System.out.println("Variables diferentes (una sola letra): " + variablesDiferentes.size());
        System.out.println("Constantes de un dígito: " + constantesUnDigito);
        System.out.println("Operadores de comparación: " + operadoresComparacion);
        System.out.println("Sentencias 'while' encontradas: " + whileCount);
        System.out.println("========================");
    }

    /**
     * Abre un diálogo gráfico para seleccionar un archivo (ej. un archivo de código Kotlin).
     * Se usa JFileChooser para una selección amigable del archivo.
     *
     * @return Ruta absoluta al archivo seleccionado o null si no se selecciona ninguno.
     */
    private static String openFileDialog() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Selecciona un archivo de código Kotlin");
        int userSelection = fileChooser.showOpenDialog(null);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().getAbsolutePath();
        }
        return null;
    }

    // =========================
    //   ANALIZADOR LÉXICO
    // =========================
    static class LexicalAnalyzer {

        private static final Pattern TOKEN_PATTERNS = Pattern.compile(
                "(?<EQ>==)|" +
                        "(?<NE>!=)|" +
                        "(?<LE><=)|" +
                        "(?<GE>>=)|" +
                        "(?<LT><)|" +
                        "(?<GT>>)|" +
                        "(?<AND>&&)|" +
                        "(?<OR>\\|\\|)|" +
                        "(?<NOT>!)|" +
                        "(?<LPAREN>\\()|" +
                        "(?<RPAREN>\\))|" +
                        "(?<LBRACE>\\{)|" +
                        "(?<RBRACE>\\})|" +
                        "(?<TRUE>true)|" +
                        "(?<FALSE>false)|" +
                        "(?<IF>if)|" +
                        "(?<WHILE>while)|" +
                        "(?<IDENT>[a-zA-Z]\\w*)|" +
                        "(?<NUMBER>\\d+)|" +
                        "(?<WS>\\s+)|" +
                        "(?<OTHER>.)"
        );

        public static List<Token> tokenize(String input) {
            List<Token> tokens = new ArrayList<>();
            Matcher matcher = TOKEN_PATTERNS.matcher(input);

            while (matcher.find()) {
                if (matcher.group("EQ") != null) {
                    tokens.add(new Token(TokenType.EQ, "=="));
                } else if (matcher.group("NE") != null) {
                    tokens.add(new Token(TokenType.NE, "!="));
                } else if (matcher.group("LE") != null) {
                    tokens.add(new Token(TokenType.LE, "<="));
                } else if (matcher.group("GE") != null) {
                    tokens.add(new Token(TokenType.GE, ">="));
                } else if (matcher.group("LT") != null) {
                    tokens.add(new Token(TokenType.LT, "<"));
                } else if (matcher.group("GT") != null) {
                    tokens.add(new Token(TokenType.GT, ">"));
                } else if (matcher.group("AND") != null) {
                    tokens.add(new Token(TokenType.AND, "&&"));
                } else if (matcher.group("OR") != null) {
                    tokens.add(new Token(TokenType.OR, "||"));
                } else if (matcher.group("NOT") != null) {
                    tokens.add(new Token(TokenType.NOT, "!"));
                } else if (matcher.group("LPAREN") != null) {
                    tokens.add(new Token(TokenType.LPAREN, "("));
                } else if (matcher.group("RPAREN") != null) {
                    tokens.add(new Token(TokenType.RPAREN, ")"));
                } else if (matcher.group("LBRACE") != null) {
                    tokens.add(new Token(TokenType.LBRACE, "{"));
                } else if (matcher.group("RBRACE") != null) {
                    tokens.add(new Token(TokenType.RBRACE, "}"));
                } else if (matcher.group("TRUE") != null) {
                    tokens.add(new Token(TokenType.TRUE, "true"));
                } else if (matcher.group("FALSE") != null) {
                    tokens.add(new Token(TokenType.FALSE, "false"));
                } else if (matcher.group("IF") != null) {
                    tokens.add(new Token(TokenType.IF, "if"));
                } else if (matcher.group("WHILE") != null) {
                    tokens.add(new Token(TokenType.WHILE, "while"));
                } else if (matcher.group("IDENT") != null) {
                    tokens.add(new Token(TokenType.IDENTIFIER, matcher.group("IDENT")));
                } else if (matcher.group("NUMBER") != null) {
                    tokens.add(new Token(TokenType.NUMBER, matcher.group("NUMBER")));
                } else if (matcher.group("WS") != null) {
                    // Ignoramos espacios en blanco
                } else if (matcher.group("OTHER") != null) {
                    tokens.add(new Token(TokenType.UNKNOWN, matcher.group("OTHER")));
                }
            }

            return tokens;
        }
    }

    // =========================
    //      TIPOS DE TOKEN
    // =========================
    enum TokenType {
        // Operadores relacionales
        EQ, NE, LT, LE, GT, GE,
        // Operadores lógicos
        AND, OR, NOT,
        // Delimitadores y palabras clave
        LPAREN, RPAREN,
        LBRACE, RBRACE,
        TRUE, FALSE,
        IF, WHILE,
        // Otros
        IDENTIFIER,
        NUMBER,
        UNKNOWN
    }

    // =========================
    //       CLASE TOKEN
    // =========================
    static record Token(TokenType type, String text) {
        @Override
        public String toString() {
            return String.format("Token(%s, \"%s\")", type, text);
        }
    }

    // =========================
    //   ANALIZADOR SINTÁCTICO
    // =========================
    static class Parser {
        private final List<Token> tokens;
        private int position;

        public Parser(List<Token> tokens) {
            this.tokens = tokens;
            this.position = 0;
        }

        // ===================================================
        //   Punto de entrada para parsear sentencias
        // ===================================================
        public void parseAllStatements() {
            while (!isAtEnd()) {
                parseStatement();
            }
        }

        /**
         * Statement -> WhileStmt | ExpressionStatement
         * WhileStmt -> 'while' '(' Expression ')' Block
         * ExpressionStatement -> Expression
         */
        private void parseStatement() {
            if (match(TokenType.WHILE)) {
                if (!match(TokenType.LPAREN)) {
                    throw new RuntimeException("Se esperaba '(' después de 'while'.");
                }
                parseExpression();
                if (!match(TokenType.RPAREN)) {
                    throw new RuntimeException("Se esperaba ')' después de la condición del while.");
                }
                parseBlock();
            } else {
                parseExpression();
            }
        }

        /**
         * Reconoce un bloque del tipo '{' ... '}' conteniendo sentencias.
         */
        private void parseBlock() {
            if (!match(TokenType.LBRACE)) {
                throw new RuntimeException("Se esperaba '{' para iniciar el bloque.");
            }
            while (!isAtEnd() && !check(TokenType.RBRACE)) {
                parseStatement();
            }
            if (!match(TokenType.RBRACE)) {
                throw new RuntimeException("Se esperaba '}' al final del bloque.");
            }
        }

        // ===================================================
        //   Gramática de Expresiones Booleanas
        // ===================================================
        /*
         * Expression -> OrExpr
         * OrExpr     -> AndExpr ( '||' AndExpr )*
         * AndExpr    -> RelExpr ( '&&' RelExpr )*
         * RelExpr    -> NotExpr ( ( EQ | NE | LT | LE | GT | GE ) NotExpr )*
         * NotExpr    -> '!' NotExpr | Primary
         * Primary    -> 'true' | 'false' | IDENTIFIER | NUMBER | '(' Expression ')'
         */
        public void parseExpression() {
            parseOrExpr();
        }

        private void parseOrExpr() {
            parseAndExpr();
            while (match(TokenType.OR)) {
                parseAndExpr();
            }
        }

        private void parseAndExpr() {
            parseRelExpr();
            while (match(TokenType.AND)) {
                parseRelExpr();
            }
        }

        private void parseRelExpr() {
            parseNotExpr();
            while (check(TokenType.EQ) || check(TokenType.NE) ||
                    check(TokenType.LT) || check(TokenType.LE) ||
                    check(TokenType.GT) || check(TokenType.GE)) {
                advance();
                parseNotExpr();
            }
        }

        private void parseNotExpr() {
            if (match(TokenType.NOT)) {
                parseNotExpr();
            } else {
                parsePrimary();
            }
        }

        private void parsePrimary() {
            if (match(TokenType.TRUE) || match(TokenType.FALSE)
                    || match(TokenType.IDENTIFIER) || match(TokenType.NUMBER)) {
                return;
            }
            if (match(TokenType.LPAREN)) {
                parseExpression();
                if (!match(TokenType.RPAREN)) {
                    throw new RuntimeException("Falta un ')' en la expresión.");
                }
                return;
            }
            throw new RuntimeException("Token inesperado: " + peek());
        }

        // ===================================================
        //   Métodos auxiliares
        // ===================================================
        private boolean isAtEnd() {
            return position >= tokens.size();
        }

        private Token peek() {
            if (isAtEnd()) {
                return new Token(TokenType.UNKNOWN, "");
            }
            return tokens.get(position);
        }

        private boolean match(TokenType type) {
            if (!isAtEnd() && peek().type() == type) {
                position++;
                return true;
            }
            return false;
        }

        private boolean check(TokenType type) {
            if (isAtEnd()) return false;
            return peek().type() == type;
        }

        private Token advance() {
            if (!isAtEnd()) position++;
            return tokens.get(position - 1);
        }
    }
}
