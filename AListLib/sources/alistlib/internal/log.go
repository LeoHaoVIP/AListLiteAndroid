package internal

import log "github.com/sirupsen/logrus"

type MyFormatter struct {
	log.Formatter
	OnLog func(level int16, msg string)
}

func (f *MyFormatter) Format(entry *log.Entry) ([]byte, error) {
	f.OnLog(int16(entry.Level), entry.Message)
	return nil, nil
}
