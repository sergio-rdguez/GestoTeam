const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    host: 'localhost', // Dirección IP local
    port: 8080,            // Puerto del servidor
    allowedHosts: 'all',   // Permitir conexiones desde cualquier host
  },
})
