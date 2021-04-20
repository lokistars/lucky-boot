/**
 * webpack.config.js webpack 配置文件
 * 所有构建工具都是基于nodejs平台运行，默认采用模块化工具 commonjs
 * 使用需要下载包:
 *  npm - style-loader css-loader -D
 *  npm i html-webpack-plugin -D
 */

//用resolve来拼接绝对路径
const {resolve} = require('path');
const HtmlWebpackPlugin = require('html-webpack-plugin');

module.exports = {
    entry: './src/index.js', //入口起点
    output: {
        filename: 'build.js', //输出文件名
        path: resolve(__dirname, 'build'),  //输出路径   __dirname 是nodejs的变量，代表当前文件的绝对路径
    },
    module: {
        rules: [
            //loader 配置 不同资源需要配置不同的 loader 处理
            {   // 匹配那些文件 处理css
                test: /\.css&/,
                //使用那些loader 处理
                use: [
                    // 创建style标签,将js中的样式资源插入,添加到head中生效
                    'style-loader',
                    // 将css文件变成commonjs模块加载的js中,里面的类似是样式字符串
                    'css-loader',
                ],
            },
            // 处理图片资源
            {
                test: /\.(jsp|png|gif)&/,
                loader: 'url-loader',
                options: {
                    limit: 8 * 1024,
                    name: '[hash:10].[txt]'
                }
            }
        ],
    },
    plugins: [
        // 使用 html打包
        // 默认会创建一个空的Html ,自动引入打包输出的所有资源
        new HtmlWebpackPlugin({
            // 复制src目录下的文件,并自动引入打包输出的所有资源
            template: './src/index.html',
        })
    ],
    //模式   开发
    mode: 'development'
    //模式   生产
    //mode:'production',
}