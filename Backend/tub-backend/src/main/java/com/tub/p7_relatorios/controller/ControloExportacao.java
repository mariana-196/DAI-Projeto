package com.tub.p7_relatorios.controller;

import com.tub.p7_relatorios.dto.FicheiroExportado;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/api/relatorios/exportar/pdf")
@CrossOrigin(origins = "*")
public class ControloExportacao {

    private static final Charset PDF_CHARSET = Charset.forName("ISO-8859-1");

    @PostMapping
    public ResponseEntity<FicheiroExportado> exportarPDF(@RequestParam(required = false, defaultValue = "OPERACIONAL") String tipo) {
        byte[] pdfBytes = gerarPdfRelatorio(tipo);
        String base64 = Base64.getEncoder().encodeToString(pdfBytes);

        String nome = "AUDITORIA".equalsIgnoreCase(tipo) ? "relatorio_auditoria.pdf" : "relatorio_operacional.pdf";
        FicheiroExportado ficheiroPDF = new FicheiroExportado(
                nome,
                base64,
                "application/pdf"
        );

        return ResponseEntity.ok(ficheiroPDF);
    }

    private byte[] gerarPdfRelatorio(String tipo) {
        String titulo = "AUDITORIA".equalsIgnoreCase(tipo) ? "Relatorio de Auditoria TUB" : "Relatorio Operacional TUB";
        List<String> linhas;
        if ("AUDITORIA".equalsIgnoreCase(tipo)) {
            linhas = List.of(
                    titulo,
                    "Gerado em: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                    "",
                    "Resumo de Auditoria",
                    "- Acessos Autorizados: 42",
                    "- Tentativas Falhadas: 3",
                    "- Modificacoes em Paineis: 5",
                    "",
                    "Ultimos Registos",
                    "admin@tub.pt | Login Efetuado | IP: 192.168.1.10",
                    "operador@tub.pt | Mensagem Publicada | IP: 192.168.1.14",
                    "",
                    "Documento gerado automaticamente pela Plataforma de Gestao Urbana TUB."
            );
        } else {
            linhas = List.of(
                    titulo,
                    "Gerado em: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                    "",
                    "Resumo Operacional",
                    "- Pontualidade media: 96%",
                    "- Lotacao media: 64%",
                    "- Passageiros transportados: 2130",
                    "",
                    "Indicadores por linha",
                    "Linha 43 | BUS-102 | Universidade do Minho | Lotacao 72% | Atraso 4 min",
                    "Linha 12 | BUS-088 | Avenida Central | Lotacao 55% | Atraso 2 min",
                    "",
                    "Documento gerado automaticamente pela Plataforma de Gestao Urbana TUB."
            );
        }

        StringBuilder stream = new StringBuilder();
        stream.append("BT\r\n");
        stream.append("/F1 18 Tf\r\n");
        stream.append("50 790 Td\r\n");
        stream.append("(").append(escapePdf(linhas.get(0))).append(") Tj\r\n");
        stream.append("/F1 11 Tf\r\n");
        for (int i = 1; i < linhas.size(); i++) {
            stream.append("0 -22 Td\r\n");
            stream.append("(").append(escapePdf(linhas.get(i))).append(") Tj\r\n");
        }
        stream.append("ET\r\n");

        byte[] streamBytes = stream.toString().getBytes(PDF_CHARSET);
        String[] objetos = {
                "<< /Type /Catalog /Pages 2 0 R >>",
                "<< /Type /Pages /Kids [3 0 R] /Count 1 >>",
                "<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] /Resources << /Font << /F1 4 0 R >> >> /Contents 5 0 R >>",
                "<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>",
                "<< /Length " + streamBytes.length + " >>\r\nstream\r\n" + stream + "endstream"
        };

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        write(out, "%PDF-1.4\r\n");
        int[] offsets = new int[objetos.length + 1];

        for (int i = 0; i < objetos.length; i++) {
            offsets[i + 1] = out.size();
            write(out, (i + 1) + " 0 obj\r\n");
            write(out, objetos[i]);
            write(out, "\r\nendobj\r\n");
        }

        int xrefOffset = out.size();
        write(out, "xref\r\n");
        write(out, "0 " + (objetos.length + 1) + "\r\n");
        write(out, "0000000000 65535 f\r\n");
        for (int i = 1; i < offsets.length; i++) {
            write(out, String.format("%010d 00000 n\r\n", offsets[i]));
        }
        write(out, "trailer\r\n");
        write(out, "<< /Size " + (objetos.length + 1) + " /Root 1 0 R >>\r\n");
        write(out, "startxref\r\n");
        write(out, xrefOffset + "\r\n");
        write(out, "%%EOF\r\n");

        return out.toByteArray();
    }

    private String escapePdf(String texto) {
        return texto.replace("\\", "\\\\")
                .replace("(", "\\(")
                .replace(")", "\\)");
    }

    private void write(ByteArrayOutputStream out, String texto) {
        byte[] bytes = texto.getBytes(PDF_CHARSET);
        out.write(bytes, 0, bytes.length);
    }
}
