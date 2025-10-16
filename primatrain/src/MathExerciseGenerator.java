import java.io.*;
import java.util.*;

// 分数类
class Fraction {
    private int numerator;
    private int denominator;
    private int whole;

    public Fraction(int numerator, int denominator) {
        if (denominator == 0) throw new IllegalArgumentException("分母不能为0");
        this.numerator = Math.abs(numerator);
        this.denominator = Math.abs(denominator);
        this.whole = 0;
        normalize();
    }

    public Fraction(int whole, int numerator, int denominator) {
        if (denominator == 0) throw new IllegalArgumentException("分母不能为0");
        this.whole = Math.abs(whole);
        this.numerator = Math.abs(numerator);
        this.denominator = Math.abs(denominator);
        normalize();
    }

    private void normalize() {
        if (numerator >= denominator) {
            whole += numerator / denominator;
            numerator %= denominator;
        }

        int gcd = gcd(numerator, denominator);
        numerator /= gcd;
        denominator /= gcd;

        if (numerator == 0) denominator = 1;
    }

    private int gcd(int a, int b) {
        return b == 0 ? a : gcd(b, a % b);
    }

    public Fraction add(Fraction other) {
        long num1 = this.getNumeratorValue();
        long den1 = this.denominator;
        long num2 = other.getNumeratorValue();
        long den2 = other.denominator;
        return new Fraction((int)(num1 * den2 + num2 * den1), (int)(den1 * den2));
    }

    public Fraction subtract(Fraction other) {
        long num1 = this.getNumeratorValue();
        long den1 = this.denominator;
        long num2 = other.getNumeratorValue();
        long den2 = other.denominator;
        return new Fraction((int)(num1 * den2 - num2 * den1), (int)(den1 * den2));
    }

    public Fraction multiply(Fraction other) {
        long num1 = this.getNumeratorValue();
        long den1 = this.denominator;
        long num2 = other.getNumeratorValue();
        long den2 = other.denominator;
        return new Fraction((int)(num1 * num2), (int)(den1 * den2));
    }

    public Fraction divide(Fraction other) {
        if (other.getNumeratorValue() == 0) throw new ArithmeticException("除数不能为0");
        long num1 = this.getNumeratorValue();
        long den1 = this.denominator;
        long num2 = other.getNumeratorValue();
        long den2 = other.denominator;
        return new Fraction((int)(num1 * den2), (int)(den1 * num2));
    }

    public int getNumeratorValue() {
        return whole * denominator + numerator;
    }

    public boolean greaterOrEqual(Fraction other) {
        return this.getNumeratorValue() * (long)other.denominator >= other.getNumeratorValue() * (long)this.denominator;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Fraction other)) return false;
        return this.getNumeratorValue() * (long)other.denominator == other.getNumeratorValue() * (long)this.denominator;
    }

    @Override
    public int hashCode() {
        Fraction simplified = new Fraction(getNumeratorValue(), denominator);
        return Objects.hash(simplified.numerator, simplified.denominator);
    }

    @Override
    public String toString() {
        if (whole == 0) return numerator == 0 ? "0" : numerator + "/" + denominator;
        return numerator == 0 ? String.valueOf(whole) : whole + "'" + numerator + "/" + denominator;
    }

    public static Fraction parseFraction(String str) {
        str = str.trim();
        if (str.contains("'")) {
            String[] parts = str.split("'");
            int whole = Integer.parseInt(parts[0]);
            String[] fractionParts = parts[1].split("/");
            return new Fraction(whole, Integer.parseInt(fractionParts[0]), Integer.parseInt(fractionParts[1]));
        } else if (str.contains("/")) {
            String[] parts = str.split("/");
            return new Fraction(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        } else {
            return new Fraction(Integer.parseInt(str), 1);
        }
    }

    public boolean isProperFraction() {
        return whole == 0 && numerator < denominator;
    }

    public boolean isZero() {
        return whole == 0 && numerator == 0;
    }

    public boolean isNegative() {
        return false; // 分数永远不会出现负数
    }
}

// 表达式生成器
class ImprovedExpressionGenerator {
    private final int range;
    private final Random random;
    private final Set<String> expressionHashes;

    public ImprovedExpressionGenerator(int range) {
        this.range = range;
        this.random = new Random();
        this.expressionHashes = new HashSet<>();
    }

    // 生成表达式（1-3个运算符）
    public String[] generateExpression(int maxAttempts) {
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            try {
                int operatorCount = random.nextInt(3) + 1; // 1-3个运算符
                String[] result = generateValidExpression(operatorCount);

                if (result != null && !expressionHashes.contains(result[2])) {
                    expressionHashes.add(result[2]);
                    return new String[]{result[0], result[1]};
                }
            } catch (Exception e) {
                // 忽略错误，继续尝试
            }
        }
        return null;
    }

    private String[] generateValidExpression(int operatorCount) {
        if (operatorCount == 1) {
            return generateValidOneOperatorExpression();
        } else if (operatorCount == 2) {
            return generateValidTwoOperatorExpression();
        } else {
            return generateValidThreeOperatorExpression();
        }
    }

    private String[] generateValidOneOperatorExpression() {
        for (int attempt = 0; attempt < 50; attempt++) {
            String operator = getRandomOperator();
            Fraction num1 = generateNumber();
            Fraction num2 = generateNumber();

            // 确保运算合法性
            if (!isOperationValid(operator, num1, num2)) {
                continue;
            }

            String expression = num1 + " " + operator + " " + num2;
            Fraction result = calculateSimple(expression);

            // 验证结果
            if (result.isNegative()) {
                continue;
            }

            String hash = generateHash(expression, result);
            return new String[]{expression + " =", result.toString(), hash};
        }
        return null;
    }

    private String[] generateValidTwoOperatorExpression() {
        for (int attempt = 0; attempt < 50; attempt++) {
            String op1 = getRandomOperator();
            String op2 = getRandomOperator();

            Fraction num1 = generateNumber();
            Fraction num2 = generateNumber();
            Fraction num3 = generateNumber();

            // 构建表达式
            String expression = buildTwoOperatorExpression(op1, op2, num1, num2, num3);

            // 验证整个表达式的合法性
            if (!isExpressionValid(expression)) {
                continue;
            }

            Fraction result = calculateComplexExpression(expression);

            // 验证最终结果
            if (result.isNegative()) {
                continue;
            }

            String hash = generateHash(expression, result);
            return new String[]{expression + " =", result.toString(), hash};
        }
        return null;
    }

    private String[] generateValidThreeOperatorExpression() {
        for (int attempt = 0; attempt < 50; attempt++) {
            String op1 = getRandomOperator();
            String op2 = getRandomOperator();
            String op3 = getRandomOperator();

            Fraction num1 = generateNumber();
            Fraction num2 = generateNumber();
            Fraction num3 = generateNumber();
            Fraction num4 = generateNumber();

            // 构建表达式
            String expression = buildThreeOperatorExpression(op1, op2, op3, num1, num2, num3, num4);

            // 验证整个表达式的合法性
            if (!isExpressionValid(expression)) {
                continue;
            }

            Fraction result = calculateComplexExpression(expression);

            // 验证最终结果
            if (result.isNegative()) {
                continue;
            }

            String hash = generateHash(expression, result);
            return new String[]{expression + " =", result.toString(), hash};
        }
        return null;
    }

    private boolean isOperationValid(String operator, Fraction num1, Fraction num2) {
        try {
            if (operator.equals("-")) {
                // 确保减法不产生负数
                return num1.greaterOrEqual(num2);
            } else if (operator.equals("÷")) {
                // 确保除法结果是真分数且除数不为0
                if (num2.isZero()) return false;
                Fraction result = num1.divide(num2);
                return result.isProperFraction();
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isExpressionValid(String expression) {
        try {
            // 检查所有中间步骤
            String tempExpression = expression;

            // 处理括号
            while (tempExpression.contains("(")) {
                int start = tempExpression.lastIndexOf("(");
                int end = tempExpression.indexOf(")", start);
                String subExpr = tempExpression.substring(start + 1, end);

                // 验证子表达式
                if (!isSimpleExpressionValid(subExpr)) {
                    return false;
                }

                Fraction subResult = calculateSimple(subExpr);
                tempExpression = tempExpression.substring(0, start) + subResult.toString() + tempExpression.substring(end + 1);
            }

            // 验证剩余表达式
            return isSimpleExpressionValid(tempExpression);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isSimpleExpressionValid(String expression) {
        try {
            String[] tokens = expression.split(" ");
            Fraction current = Fraction.parseFraction(tokens[0]);

            for (int i = 1; i < tokens.length; i += 2) {
                String operator = tokens[i];
                Fraction next = Fraction.parseFraction(tokens[i + 1]);

                if (!isOperationValid(operator, current, next)) {
                    return false;
                }

                current = calculateSimple(current + " " + operator + " " + next);

                if (current.isNegative()) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String getRandomOperator() {
        String[] operators = {"+", "-", "×", "÷"};
        return operators[random.nextInt(operators.length)];
    }

    private Fraction generateNumber() {
        if (random.nextBoolean()) {
            // 生成自然数
            return new Fraction(random.nextInt(range - 1) + 1, 1);
        } else {
            // 生成真分数
            int denominator = random.nextInt(range - 2) + 2;
            int numerator = random.nextInt(denominator - 1) + 1;
            return new Fraction(numerator, denominator);
        }
    }

    private String buildTwoOperatorExpression(String op1, String op2, Fraction num1, Fraction num2, Fraction num3) {
        boolean needParentheses = needParentheses(op1, op2);

        if (needParentheses) {
            return "(" + num1 + " " + op1 + " " + num2 + ") " + op2 + " " + num3;
        } else {
            return num1 + " " + op1 + " " + num2 + " " + op2 + " " + num3;
        }
    }

    private String buildThreeOperatorExpression(String op1, String op2, String op3, Fraction num1, Fraction num2, Fraction num3, Fraction num4) {
        int structure = random.nextInt(4);
        switch (structure) {
            case 0:
                return num1 + " " + op1 + " " + num2 + " " + op2 + " " + num3 + " " + op3 + " " + num4;
            case 1:
                return "(" + num1 + " " + op1 + " " + num2 + ") " + op2 + " " + num3 + " " + op3 + " " + num4;
            case 2:
                return num1 + " " + op1 + " (" + num2 + " " + op2 + " " + num3 + ") " + op3 + " " + num4;
            case 3:
                return "(" + num1 + " " + op1 + " " + num2 + ") " + op2 + " (" + num3 + " " + op3 + " " + num4 + ")";
            default:
                return num1 + " " + op1 + " " + num2 + " " + op2 + " " + num3 + " " + op3 + " " + num4;
        }
    }

    private boolean needParentheses(String op1, String op2) {
        Map<String, Integer> precedence = new HashMap<>();
        precedence.put("+", 1);
        precedence.put("-", 1);
        precedence.put("×", 2);
        precedence.put("÷", 2);
        return precedence.get(op2) > precedence.get(op1);
    }

    private Fraction calculateSimple(String expression) {
        String[] parts = expression.split(" ");
        Fraction num1 = Fraction.parseFraction(parts[0]);
        String op = parts[1];
        Fraction num2 = Fraction.parseFraction(parts[2]);

        switch (op) {
            case "+":
                return num1.add(num2);
            case "-":
                return num1.subtract(num2);
            case "×":
                return num1.multiply(num2);
            case "÷":
                return num1.divide(num2);
            default:
                throw new IllegalArgumentException("未知运算符: " + op);
        }
    }

    private Fraction calculateComplexExpression(String expression) {
        // 处理括号表达式
        while (expression.contains("(")) {
            int start = expression.lastIndexOf("(");
            int end = expression.indexOf(")", start);
            String subExpr = expression.substring(start + 1, end);
            Fraction subResult = calculateSimple(subExpr);
            expression = expression.substring(0, start) + subResult.toString() + expression.substring(end + 1);
        }

        // 计算剩余表达式
        String[] tokens = expression.split(" ");
        Fraction result = Fraction.parseFraction(tokens[0]);

        for (int i = 1; i < tokens.length; i += 2) {
            String operator = tokens[i];
            Fraction nextNum = Fraction.parseFraction(tokens[i + 1]);
            result = calculateSimple(result + " " + operator + " " + nextNum);
        }

        return result;
    }

    private String generateHash(String expression, Fraction result) {
        return expression.replaceAll("\\s+", "").replace("(", "").replace(")", "") + "=" + result.toString();
    }

    // 静态方法用于批改
    public static Fraction calculateExpressionForGrading(String expression) {
        expression = expression.replace("=", "").trim();
        ImprovedExpressionGenerator generator = new ImprovedExpressionGenerator(10);
        return generator.calculateComplexExpression(expression);
    }
}

// 主程序
public class MathExerciseGenerator {
    public static void main(String[] args) {
        if (args.length == 0) {
            printHelp();
            return;
        }

        try {
            // 解析参数
            if (args.length == 4 && args[0].equals("-n") && args[2].equals("-r")) {
                // 生成题目模式: -n 数量 -r 范围
                int count = Integer.parseInt(args[1]);
                int range = Integer.parseInt(args[3]);
                generateExercises(count, range);
            } else if (args.length == 4 && args[0].equals("-e") && args[2].equals("-a")) {
                // 批改模式: -e 题目文件 -a 答案文件
                String exerciseFile = args[1];
                String answerFile = args[3];
                gradeExercises(exerciseFile, answerFile);
            } else {
                System.out.println("错误：参数格式不正确");
                printHelp();
            }
        } catch (Exception e) {
            System.out.println("错误: " + e.getMessage());
            printHelp();
        }
    }

    private static void printHelp() {
        System.out.println("小学数学四则运算题目生成器");
        System.out.println("==========================");
        System.out.println("使用方法:");
        System.out.println();
        System.out.println("1. 生成题目功能:");
        System.out.println("   java MathExerciseGenerator -n <题目数量> -r <数值范围>");
        System.out.println("   示例: java MathExerciseGenerator -n 10 -r 10");
        System.out.println();
        System.out.println("2. 批改答案功能:");
        System.out.println("   java MathExerciseGenerator -e <exercisefile>.txt -a <answerfile>.txt");
        System.out.println("   示例: java MathExerciseGenerator -e exercisefile.txt -a answerfile.txt");
        System.out.println();
        System.out.println("注意:");
        System.out.println("- 批改功能可以批改任何符合格式的题目文件和答案文件");
    }

    // 生成题目功能
    private static void generateExercises(int count, int range) {
        if (range <= 1) {
            System.out.println("错误：数值范围必须大于1");
            return;
        }

        if (count <= 0) {
            System.out.println("错误：题目数量必须大于0");
            return;
        }

        System.out.println("正在生成 " + count + " 道题目，数值范围: 1-" + range);

        ImprovedExpressionGenerator generator = new ImprovedExpressionGenerator(range);
        List<String> exercises = new ArrayList<>();
        List<String> answers = new ArrayList<>();

        long startTime = System.currentTimeMillis();
        int generated = 0;

        for (int i = 0; i < count; i++) {
            String[] result = generator.generateExpression(100);

            if (result != null) {
                exercises.add((i+1) + ". " + result[0]);
                answers.add((i+1) + ". " + result[1]);
                generated++;
            }
        }

        long endTime = System.currentTimeMillis();

        try {
            writeToFile("Exercises.txt", exercises);
            writeToFile("Answers.txt", answers);

            System.out.println("生成完成！");
            System.out.println("成功生成: " + generated + " 道题目");
            System.out.println("题目文件: Exercises.txt");
            System.out.println("答案文件: Answers.txt");

        } catch (IOException e) {
            System.out.println("保存文件失败: " + e.getMessage());
        }
    }

    // 批改答案功能
    private static void gradeExercises(String exerciseFile, String answerFile) {
        try {
            System.out.println("正在批改答案...");
            System.out.println("题目文件: " + exerciseFile);
            System.out.println("答案文件: " + answerFile);

            // 读取题目文件
            List<String> exercises = readFile(exerciseFile);
            // 读取学生答案文件
            List<String> studentAnswers = readFile(answerFile);

            if (exercises.isEmpty()) {
                System.out.println("错误：题目文件为空或不存在");
                return;
            }

            if (studentAnswers.isEmpty()) {
                System.out.println("错误：答案文件为空或不存在");
                return;
            }

            List<Integer> correct = new ArrayList<>();
            List<Integer> wrong = new ArrayList<>();

            int total = Math.min(exercises.size(), studentAnswers.size());

            System.out.println("发现 " + exercises.size() + " 道题目和 " + studentAnswers.size() + " 个答案");

            for (int i = 0; i < total; i++) {
                String exerciseLine = exercises.get(i);
                String studentAnswerLine = studentAnswers.get(i);

                // 解析题目表达式
                String exercise = parseExercise(exerciseLine);
                String studentAnswer = parseAnswer(studentAnswerLine);

                if (exercise == null) {
                    wrong.add(i + 1);
                    continue;
                }

                if (studentAnswer == null) {
                    wrong.add(i + 1);
                    continue;
                }

                try {
                    // 计算正确答案
                    Fraction correctAnswer = ImprovedExpressionGenerator.calculateExpressionForGrading(exercise);
                    // 解析学生答案
                    Fraction studentAnswerFraction = Fraction.parseFraction(studentAnswer);

                    if (correctAnswer.equals(studentAnswerFraction)) {
                        correct.add(i + 1);
                    } else {
                        wrong.add(i + 1);
                        System.out.println("第 " + (i+1) + " 题错误:");
                        System.out.println("  学生答案: " + studentAnswer);
                        System.out.println("  正确答案: " + correctAnswer);
                    }
                } catch (Exception e) {
                    wrong.add(i + 1);
                }
            }

            // 保存批改结果
            saveGradeResult(correct, wrong);

            System.out.println("\n批改完成！");
            System.out.println("总题数: " + total);
            System.out.println("正确: " + correct.size() + " 题");
            System.out.println("错误: " + wrong.size() + " 题");

            if (total > 0) {
                double accuracy = (double) correct.size() / total * 100;
                System.out.printf("正确率: %.1f%%\n", accuracy);
            }

            System.out.println("批改结果已保存到 Grade.txt");

        } catch (IOException e) {
            System.out.println("读取文件失败: " + e.getMessage());
        }
    }

    // 解析题目表达式（支持多种格式）
    private static String parseExercise(String exerciseLine) {
        if (exerciseLine == null || exerciseLine.trim().isEmpty()) {
            return null;
        }

        String line = exerciseLine.trim();

        // 格式1: "1. 3/4 + 1/2 ="
        if (line.matches("^\\d+\\.\\s+.*")) {
            String[] parts = line.split("\\.\\s+", 2);
            if (parts.length == 2) {
                return parts[1].replace("=", "").trim();
            }
        }

        // 格式2: "3/4 + 1/2 ="
        if (line.contains("=")) {
            return line.replace("=", "").trim();
        }

        // 格式3: 没有等号的情况
        return line;
    }

    // 解析答案（支持多种格式）
    private static String parseAnswer(String answerLine) {
        if (answerLine == null || answerLine.trim().isEmpty()) {
            return null;
        }

        String line = answerLine.trim();

        // 格式1: "1. 1'1/4"
        if (line.matches("^\\d+\\.\\s+.*")) {
            String[] parts = line.split("\\.\\s+", 2);
            if (parts.length == 2) {
                return parts[1].trim();
            }
        }

        // 格式2: 直接答案
        return line;
    }

    private static void saveGradeResult(List<Integer> correct, List<Integer> wrong) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter("Grade.txt"))) {
            writer.println("Correct: " + correct.size() + formatList(correct));
            writer.println("Wrong: " + wrong.size() + formatList(wrong));
        }
    }

    private static String formatList(List<Integer> list) {
        if (list.isEmpty()) return " ()";
        StringBuilder sb = new StringBuilder(" (");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(list.get(i));
        }
        sb.append(")");
        return sb.toString();
    }

    private static void writeToFile(String filename, List<String> content) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (String line : content) {
                writer.println(line);
            }
        }
    }

    private static List<String> readFile(String filename) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("错误：文件不存在 - " + filename);
            throw e;
        }
        return lines;
    }
}