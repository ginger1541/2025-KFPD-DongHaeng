// PM2 Ecosystem 설정 파일
// 사용법: pm2 start ecosystem.config.js --env production

module.exports = {
  apps: [
    {
      // 앱 설정
      name: 'dongheng-backend',
      script: './dist/index.js',

      // 인스턴스 설정
      instances: 2,                    // CPU 코어 수만큼 (또는 'max')
      exec_mode: 'cluster',            // 클러스터 모드

      // 환경변수
      env_production: {
        NODE_ENV: 'production',
        PORT: 3000,
      },

      // 환경변수 파일
      env_file: '.env.production',

      // 재시작 정책
      autorestart: true,
      watch: false,                    // 프로덕션에서는 false
      max_memory_restart: '1G',        // 메모리 1GB 초과 시 재시작

      // 로그 설정
      error_file: './logs/pm2-error.log',
      out_file: './logs/pm2-out.log',
      log_date_format: 'YYYY-MM-DD HH:mm:ss Z',
      merge_logs: true,

      // 재시작 전략
      min_uptime: '10s',               // 최소 가동 시간
      max_restarts: 10,                // 최대 재시작 횟수
      restart_delay: 4000,             // 재시작 지연 (ms)

      // 그레이스풀 셧다운
      kill_timeout: 5000,
      wait_ready: true,
      listen_timeout: 3000,

      // 시간 설정
      time: true,

      // 크론 재시작 (선택사항)
      // cron_restart: '0 4 * * *',    // 매일 새벽 4시 재시작
    },
  ],

  // 배포 설정 (선택사항)
  deploy: {
    production: {
      user: 'dongheng',
      host: 'your-server-ip',
      ref: 'origin/main',
      repo: 'https://github.com/yourusername/companion-backend.git',
      path: '/home/dongheng/app',
      'pre-deploy-local': '',
      'post-deploy': 'npm install && npm run build && pm2 reload ecosystem.config.js --env production',
      'pre-setup': '',
    },
  },
};
