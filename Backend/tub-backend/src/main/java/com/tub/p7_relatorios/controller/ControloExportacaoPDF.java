package com.tub.p7_relatorios.controller;

import com.tub.p7_relatorios.dto.FicheiroPDF;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
public class ControloExportacaoPDF {

    private static final Charset PDF_CHARSET = Charset.forName("ISO-8859-1");

    @PostMapping
    public ResponseEntity<FicheiroPDF> exportarPDF() {
        byte[] pdfBytes = gerarPdfRelatorio();
        String base64 = Base64.getEncoder().encodeToString(pdfBytes);

        FicheiroPDF ficheiroPDF = new FicheiroPDF(
                "relatorio_sistema.pdf",
                base64,
                "application/pdf"
        );

        return ResponseEntity.ok(ficheiroPDF);
    }

    private byte[] gerarPdfRelatorio() {
        List<String> linhas = List.of(
                "Relatorio Operacional TUB",
                "Gerado em: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                "",
                "Resumo",
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

        StringBuilder stream = new StringBuilder();
        stream.append("BT\n");
        stream.append("/F1 18 Tf\n");
        stream.append("50 790 Td\n");
        stream.append("(").append(escapePdf(linhas.get(0))).append(") Tj\n");
        stream.append("/F1 11 Tf\n");
        for (int i = 1; i < linhas.size(); i++) {
            stream.append("0 -22 Td\n");
            stream.append("(").append(escapePdf(linhas.get(i))).append(") Tj\n");
        }
        stream.append("ET\n");

        byte[] streamBytes = stream.toString().getBytes(PDF_CHARSET);
        String[] objetos = {
                "<< /Type /Catalog /Pages 2 0 R >>",
                "<< /Type /Pages /Kids [3 0 R] /Count 1 >>",
                "<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] /Resources << /Font << /F1 4 0 R >> >> /Contents 5 0 R >>",
                "<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>",
                "<< /Length " + streamBytes.length + " >>\nstream\n" + stream + "endstream"
        };

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        write(out, "%PDF-1.4\n");
        int[] offsets = new int[objetos.length + 1];

        for (int i = 0; i < objetos.length; i++) {
            offsets[i + 1] = out.size();
            write(out, (i + 1) + " 0 obj\n");
            write(out, objetos[i]);
            write(out, "\nendobj\n");
        }

        int xrefOffset = out.size();
        write(out, "xref\n");
        write(out, "0 " + (objetos.length + 1) + "\n");
        write(out, "0000000000 65535 f \n");
        for (int i = 1; i < offsets.length; i++) {
            write(out, String.format("%010d 00000 n \n", offsets[i]));
        }
        write(out, "trailer\n");
        write(out, "<< /Size " + (objetos.length + 1) + " /Root 1 0 R >>\n");
        write(out, "startxref\n");
        write(out, String.valueOf(xrefOffset));
        write(out, "\n%%EOF\n");

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
