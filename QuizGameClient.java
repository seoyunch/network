package programimng.HW;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class QuizGameClient {

    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 1234);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
             Scanner scanner = new Scanner(System.in)) {

            // 게임 시작 여부 묻는 메시지 출력
            System.out.println(in.readLine());  // 서버에서 'yes'/'no' 입력 받으라는 메시지
            String response = scanner.nextLine().trim();  // 클라이언트 입력 받기
            out.write(response + "\n");
            out.flush();

            // 게임 취소 처리
            if (response.equalsIgnoreCase("no")) {
                System.out.println("Game cancelled.");
                return;  // 게임 종료
            }

            // 게임 시작 후 문제를 하나씩 받고 답을 입력하는 과정
            String serverMessage;
            while ((serverMessage = in.readLine()) != null) {
                // 게임 종료 메시지 처리
                if (serverMessage.contains("finished")) {
                    System.out.println(serverMessage);  // 게임 종료 메시지 출력
                    break;
                }

                // 문제 출력
                System.out.println(serverMessage);  // 문제 출력
                String answer = scanner.nextLine().trim();  // 답 입력
                out.write(answer + "\n");
                out.flush();

                // 결과 받기 (정답/오답)
                String result = in.readLine();
                System.out.println(result);  // 결과 출력
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
