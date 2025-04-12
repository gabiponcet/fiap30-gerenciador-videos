  output "api_url" {
    value = aws_api_gateway_deployment.gerenciador_deployment.invoke_url
  }