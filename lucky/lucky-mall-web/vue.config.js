/**
 * 配置参考: https://cli.vuejs.org/zh/config/
 */
module.exports = {
  publicPath: process.env.NODE_ENV === 'production' ? './' : '/',
  chainWebpack: config => {
    const svgRule = config.module.rule('svg')
    svgRule.uses.clear()
    svgRule
      .test(/\.svg$/)
      .use('svg-sprite-loader')
      .loader('svg-sprite-loader')
  },
  productionSourceMap: false,
  devServer: {
    open: true,
    port: 6005,
    proxy: {
      '/api': {
        target: 'http://localhost:6001',
        changeOrigin: true,
        rewrite: path => path.replace(/^\/api/, '')
      }
    },
    overlay: {
      errors: true,
      warnings: true
    }
  }
}
