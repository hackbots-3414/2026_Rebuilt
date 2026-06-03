ssh -T admin@10.34.14.2 << EOF
  sleep 1
  echo "Connected to 10.34.14.2."
  sleep 0.25
  cd /etc
  sleep 2
  echo "Removing demobot and creating testbot..."
  rm demobot
  sleep 0.25
  touch testbot
  sleep 0.25
  echo "Testbot created successfully. Restart Robot code for changes to take effect."
  sleep 3
  echo "Exiting from ssh session..."
  sleep 2
  exit
EOF