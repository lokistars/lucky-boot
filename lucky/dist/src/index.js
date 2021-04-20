/**
 * index.js :webpack 入口起点文件
 *  1.运行指令:
 *    webpack ./src/index.js -o ./build/build.js --mode=development
 *    webpack ./src/index.js -o ./build/build.js --mode=production
 */

function add(x, y) {
    return x + y;
}

add(3, 5);