package programimng.HW;

import java.io.*;
import java.net.*;

public class QuizGameServer {

    // 문제에 대한 점수를 계산하는 함수
    public static int calculateScore(String answer, int index) {
        int[] points = {3, 1, 2, 1, 2}; // 문제별 점수
        String[] correctAnswers = {"Egypt", "Red", "Seoul", "Cat", "Honey"}; // 정답 목록

        answer = answer.trim();  // 공백 제거

        // 정답이 맞으면 점수 반환
        if (answer.equalsIgnoreCase(correctAnswers[index])) {
            return points[index];  // 맞으면 해당 점수 반환
        } else {
            return 0;  // 틀리면 0점 반환
        }
    }

    public static void main(String[] args) {
        String[] questions = {
            "Which country is famous for the pyramids?",
            "What color is an apple?",
            "What is the capital of South Korea?",
            "What animal makes the 'meow' sound?",
            "What do bees make?"
        };
        int totalScore = 0;

        try (ServerSocket serverSocket = new ServerSocket(1234)) {
            System.out.println("Server started. Waiting for connection...");

            try (Socket clientSocket = serverSocket.accept();
                 BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))) {

                System.out.println("Client connected. Waiting for start command...");
                out.write("Press 'yes' to start the game or 'no' to cancel:\n");
                out.flush();

                // 클라이언트의 게임 시작 여부 입력 대기
                String clientResponse = in.readLine().trim();
                if (clientResponse.equalsIgnoreCase("no")) {
                    out.write("Game cancelled by client.\n");
                    out.flush();
                    return; // 게임 종료
                }

                System.out.println("Game starting...");
                out.flush();

                // 문제 하나씩 보내고, 답을 받아서 점수를 계산하고 결과를 클라이언트에 전송
                for (int i = 0; i < questions.length; i++) {
                    out.write("#" + (i + 1) + ". " + questions[i] + "\n");
                    out.flush();

                    // 클라이언트로부터 답을 받음
                    String clientAnswer = in.readLine();
                    if (clientAnswer == null || clientAnswer.trim().isEmpty()) {
                        System.out.println("Client disconnected or invalid answer.");
                        break;
                    }

                    // 정답 판단 후 점수 계산
                    int score = calculateScore(clientAnswer, i);
                    totalScore += score;
                    out.write((score > 0 ? "Correct!\n" : "Incorrect.\n"));
                    out.flush();
                }

                // 모든 문제 후 총 점수 전달
                out.write("Quiz finished. Your total score is: " + totalScore + "\n");
                out.flush();

            } catch (IOException e) {
                System.err.println("Error during communication: " + e.getMessage());
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }
}
