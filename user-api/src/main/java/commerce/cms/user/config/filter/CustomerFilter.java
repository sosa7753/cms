package commerce.cms.user.config.filter;

import commerce.cms.domain.config.JwtAuthenticationProvider;
import commerce.cms.domain.domain.common.UserVo;
import commerce.cms.user.service.customer.CustomerService;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import lombok.RequiredArgsConstructor;

@WebFilter(urlPatterns = "/customer/*")
@RequiredArgsConstructor
public class CustomerFilter implements Filter {

  private final JwtAuthenticationProvider provider;
  private final CustomerService customerService;

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    HttpServletRequest req = (HttpServletRequest) request;
    String token = req.getHeader("X-AUTH-TOKEN");
    if(!provider.validateToken(token)) {
      throw new ServletException("Invalid token");
    }

    UserVo vo = provider.getUserVo(token);
    customerService.findByIdAndEmail(vo.getId(), vo.getEmail()).orElseThrow(
        ()-> new SecurityException("Invalid access")
    );
    chain.doFilter(request, response); // 필터를 다음 필터나 서블릿으로 넘김.
  }
}
