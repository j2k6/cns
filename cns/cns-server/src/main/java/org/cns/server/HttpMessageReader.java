package org.cns.server;

import java.nio.ByteBuffer;
import java.util.Queue;

import org.apache.log4j.Logger;
import org.cns.api.server.MessageReader;

/**
 * Парсер сообщений для HTTP-протокола
 * 
 * @author ivanovd
 *
 */
public class HttpMessageReader implements MessageReader {

    private static final Logger logger = Logger.getLogger(HttpMessageReader.class);

    private static final String[][] HTTP_RESP_CODES = { { "200", "OK" }, { "400", "Bad Request" },
            { "411", "Length Required" }, { "500", "Internal Server Error" }, { "501", "Not Implemented" },
            { "503", "Service Unavailable" }, { "504", "Gateway Timeout" }, { "505", "HTTP Version Not Supported" } };

    private ByteBuffer buffer;
    private HttpMessage tempMessage;
    private boolean messageReady;

    public HttpMessageReader(int bufferSize) {
        this.buffer = ByteBuffer.allocate(bufferSize);
        this.tempMessage = new HttpMessage();
    }

    @Override
    public ByteBuffer getBuffer() {
        return buffer;
    }

    @Override
    public void processBuffer(int bytesRead, Queue<String> incomingMessages) {
        buffer.flip();

        byte[] bytes = new byte[buffer.limit()];
        buffer.get(bytes);

        // аккумулируем в запросе полученные байты
        tempMessage.add(new String(bytes));

        // проверяем, накопилось ли в запросе достаточно данных для соответствия HTTP-запросу
        if (tempMessage.isHttpCompliant()) {
            incomingMessages.add(tempMessage.extractRequest());
            messageReady = true;
        } else {
            messageReady = false;
        }

        buffer.clear();
    }

    @Override
    public boolean isMessageReady() {
        return messageReady;
    }

    /**
     * Вспомогательный класс для хранения результатов разбора HTTP-запроса. В случае, если накопленный запрос не
     * удовлетворяет RFC, проверяется наличие нового запроса, а все, что накопили - отбрасываем.
     * 
     * 
     * @author ivanovd
     *
     */
    public class HttpMessage {

        private boolean methodFound;
        private boolean versionFound;
        private boolean contentLenghtFound;
        private boolean bodyRead;

        // длина контента
        private int clValue = -1;

        private int httpRespCode = 200;

        private StringBuilder request = new StringBuilder();

        /**
         * 
         * 
         * @return true если все нужные части HTTP-запроса были найдены и прочитано тело запроса до конца; false в
         *         остальных случаях
         */
        public boolean isHttpCompliant() {
            return methodFound && versionFound && contentLenghtFound && bodyRead;
        }

        /**
         * Достает весь ХТТП-запрос, удаляет его из буфера
         * 
         * @return
         */
        public String extractRequest() {
            int delimiterIdx = request.indexOf("\r\n\r\n");
            int endIdx = delimiterIdx + 4 + clValue;
            String result = request.substring(0, endIdx);

            request.delete(0, endIdx);

            methodFound = false;
            versionFound = false;
            contentLenghtFound = false;
            bodyRead = false;
            clValue = 0;
            httpRespCode = 200;

            return result;
        }

        /**
         * При добавлении части запроса происходит попытка его распарсить - только при получении \r\n\r\n -
         * обязательного маркера разделения заголовков и тела запроса.
         * 
         * @param reqPart
         */
        public void add(String reqPart) {
            request.append(reqPart);

            // нашли потенциальное разделение между заголовком запроса и телом - пробуем проверить, что запрос верно
            // сформирован
            int delimiterIdx = request.indexOf("\r\n\r\n");
            if (delimiterIdx > 0) {

                ////////////////////////////////
                // обработка строки запроса

                // вычитываем реквест-лайн - Method SP Request-URI SP HTTP-Version CRLF
                // если не удается распарсить - выставляем код ошибки 400 и выкидываем остатки запроса до переводов
                // строк
                String requestLine = request.substring(0, request.indexOf("\r\n"));
                String rlParts[] = requestLine.split("\\s");
                if (rlParts.length != 3) {
                    httpRespCode = 400;
                    request.delete(0, delimiterIdx + 4);
                    return;
                }

                // проверяем Method - кроме гета и поста ничего не поддерживаем
                if (!"GET".equals(rlParts[0]) && !"POST".equals(rlParts[0])) {
                    httpRespCode = 501;
                    request.delete(0, delimiterIdx + 4);
                    return;
                }
                methodFound = true;

                // проверяем Request-URI
                // TODO: not-implemented

                // проверям версию HTTP - поддерживаем только 1.1
                if (!"HTTP/1.1".equals(rlParts[2])) {
                    httpRespCode = 505;
                    request.delete(0, delimiterIdx + 4);
                    return;
                }
                versionFound = true;

                ////////////////////////////////
                // обработка прочих заголовков

                String requestHeaders = request.substring(request.indexOf("\r\n") + 2, delimiterIdx+2);

                // ищем среди заголовков Host
                // TODO: not-implemented

                // ищем Transfer-Encoding - если такой заголовок есть - выкидываем 501, поддерживаем только явно
                // указанный ищем Content-Length
                if (requestHeaders.indexOf("Transfer-Encoding") > -1) {
                    httpRespCode = 501;
                    request.delete(0, delimiterIdx + 4);
                    return;
                }

                // если не указан длина контента в запросе - кидаем ошибку
                int contLenIdx = requestHeaders.indexOf("Content-Length");
                if (contLenIdx == -1) {
                    httpRespCode = 411;
                    request.delete(0, delimiterIdx + 4);
                    return;
                } else {
                    // достаем длину данных
                    int contLenEndIdx = requestHeaders.indexOf("\r\n", contLenIdx);
                    this.clValue = -1;
                    try {
                        clValue = Integer.parseInt(requestHeaders.substring(contLenIdx + 16, contLenEndIdx));
                    } catch (Exception e) {
                        logger.error("Error trying to calc Content-Length header value.", e);
                    }

                    if (clValue == -1) {
                        httpRespCode = 411;
                        request.delete(0, delimiterIdx + 4);
                        return;
                    }
                }
                contentLenghtFound = true;

                ////////////////////////////////
                // обработка тела запроса - проверяем, накопилось ли данных в запросе, сколько указано в Content-Length
                String requestBody = request.substring(delimiterIdx + 4);
                if (requestBody.length() >= clValue)
                    bodyRead = true;
            }

        }
    }
}
