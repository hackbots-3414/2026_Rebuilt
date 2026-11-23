ssh -T admin@10.34.14.2 << EOF
  sleep 1
  echo "Connected to 10.34.14.2."
  sleep 0.25
  cd /etc
  sleep 2
  echo "Removing Demobot and Testbot if they exist..."
  rm testbot
  sleep 0.25
  rm demobot
  sleep 0.25
  echo "Removed Demobot and Testbot. Restart Robot code in order for changes to take effect."
  sleep 3
  echo "Disconnecting from ssh session..."
  sleep 1
  exit
EOF