module.exports = {
  entry: './src/main/resources/static/main.ts',
  output: {
    path: __dirname + '/build/resources/main/static',
    filename: 'app.js'
  },
  mode: 'development',
  module: {
    rules: [
      {
        test: /\.ts$/,
        use: 'ts-loader',
      },
    ],
  },
  resolve: {
    extensions: ['*', '.js', '.json', '.ts'],
    modules: [
      "node_modules"
    ],
    alias: {
      'vue$': 'vue/dist/vue.esm.js'
    }
  },
  optimization: {
    minimize: false
  }
};
