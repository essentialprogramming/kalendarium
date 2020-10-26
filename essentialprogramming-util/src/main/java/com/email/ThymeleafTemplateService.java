package com.email;

import com.lowagie.text.DocumentException;
import com.util.exceptions.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;

class ThymeleafTemplateService implements TemplateService {

    @Autowired
    private TemplateEngine htmlTemplateEngine;

    @Override
    public String generateHTML(Map<String, Object> content, Template template) {
        return generateHTML(content, template, Locale.getDefault());
    }

    @Override
    public String generateHTML(Map<String, Object> content, Template template, Locale locale) {
        String generatedHTML;
        final Context context = new Context(locale);
        template.getFragment().ifPresent((fragment) -> context.setVariable("fragment", fragment));
        template.getPage().ifPresent((page) -> context.setVariable("page", page));
        content.forEach((key, value) -> context.setVariable(key, value));

        try {
            // If template has no master-template, it will be rendered as it is
            String templateToRender = template.getMaster().orElse(template).getPage().orElseThrow(() -> new  ServiceException(TemplateErrorCode.UNABLE_TO_GENERATE_EMAIL_TEMPLATE));
            generatedHTML = this.htmlTemplateEngine.process(templateToRender, context);
        } catch (RuntimeException e) {
            throw new ServiceException(TemplateErrorCode.UNABLE_TO_GENERATE_EMAIL_TEMPLATE, e);
        }
        if (generatedHTML == null) {
            throw new ServiceException(TemplateErrorCode.UNABLE_TO_GENERATE_EMAIL_TEMPLATE);
        }
        return generatedHTML;
    }

    @Override
    public byte[] generatePDF(Map<String, Object> content, Template template, Locale locale) {
        String generatedHTML = generateHTML(content, template, locale);
        byte[] pdfByteArray;

        ByteArrayOutputStream outputStream;
        try {
            outputStream = new ByteArrayOutputStream();
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(generatedHTML);
            renderer.layout();
            renderer.createPDF(outputStream);
            pdfByteArray = outputStream.toByteArray();
            outputStream.close();
        } catch (DocumentException | IOException e) {
            throw new ServiceException(TemplateErrorCode.UNABLE_TO_GENERATE_PDF);
        }
        return pdfByteArray;
    }
}
