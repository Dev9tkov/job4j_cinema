package ru.job4j.servlet;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.w3c.dom.ls.LSOutput;
import ru.job4j.model.Account;
import ru.job4j.model.Hall;
import ru.job4j.store.DbStore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HallServlet extends HttpServlet {
    /**
     * Достаем из БД таблицу Hall, преобразуем ее в JSON-объект и передаем клиенту
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/json");
        PrintWriter pw = resp.getWriter();
        List<Hall> list = DbStore.instOf().places();
        Collections.sort(list);
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode arrayNode = objectMapper.createArrayNode();
        for (Hall val : list) {
            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("row", val.getRow());
            objectNode.put("place", val.getPlace());
            objectNode.put("price", val.getPrice());
            objectNode.put("free", val.isFree());
            arrayNode.add(objectNode);
        }
        String json = objectMapper.writeValueAsString(arrayNode);
        pw.append(json);
        pw.flush();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/json");
        PrintWriter pw = resp.getWriter();
        BufferedReader reader = req.getReader();
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(sb.toString());
        int row = node.get("row").asInt();
        int place = node.get("place").asInt();
        int price = node.get("price").asInt();
        String name = node.get("username").asText();
        String telNumber = node.get("phone").asText();
        Hall hall = new Hall(row, place, price, false);
        Account account = new Account(name, telNumber, null);
        ObjectNode resNode = mapper.createObjectNode();
        try {
            DbStore.instOf().makePayment(hall, account);
            resNode.put("success", true);
            resNode.put("row", row);
            resNode.put("place", place);
        } catch (Exception e) {
            resNode.put("success", false);
            resNode.put("row", row);
            resNode.put("place", place);
        }
        String json = mapper.writeValueAsString(resNode);
        pw.append(json);
        pw.flush();
    }
}
