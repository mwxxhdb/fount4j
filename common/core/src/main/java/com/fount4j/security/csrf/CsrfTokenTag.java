package com.fount4j.security.csrf;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.processor.element.AbstractElementTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.StandardDialect;
import org.thymeleaf.templatemode.TemplateMode;

import java.util.HashSet;
import java.util.Set;

import static com.fount4j.security.csrf.CsrfToken.ATTRIBUTE_NAME;

@Slf4j
@Component
public class CsrfTokenTag extends AbstractProcessorDialect {

    protected CsrfTokenTag() {
        super("Fount4j customized tags", "ft", StandardDialect.PROCESSOR_PRECEDENCE);
    }

    @Override
    public Set<IProcessor> getProcessors(String dialectPrefix) {
        final Set<IProcessor> processor = new HashSet<>();
        processor.add(new CsrfTag(dialectPrefix));
        return processor;
    }

    public static class CsrfTag extends AbstractElementTagProcessor {

        public CsrfTag(String dialectPrefix) {
            super(TemplateMode.HTML, dialectPrefix, "csrf", true, null, false, 10000);
        }

        @Override
        protected void doProcess(ITemplateContext context, IProcessableElementTag tag, IElementTagStructureHandler structureHandler) {
            Object csrf = context.getVariable(ATTRIBUTE_NAME);
            if (!(csrf instanceof CsrfToken)) {
                structureHandler.replaceWith("", false);
                return;
            }
            CsrfToken token = (CsrfToken) csrf;
            String hidden = "<input type=\"hidden\" id=\"_fount4j_csrf_token\" name=\"" + token.getParameterName() + "\" value=\"" + token.getToken() + "\"/>";
            structureHandler.replaceWith(hidden, false);
        }
    }
}
