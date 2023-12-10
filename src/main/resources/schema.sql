-- jdbc 템플릿 테스트를 위한 테이블
CREATE TABLE IF NOT EXISTS messages (
    id       VARCHAR(60)  PRIMARY KEY,
    text     VARCHAR      NOT NULL
    );

-- spring data jpa 테스트를 위한 테이블
CREATE TABLE IF NOT EXISTS messagesDb (
    id      VARCHAR(60)  DEFAULT RANDOM_UUID() PRIMARY KEY,
    text    VARCHAR      NOT NULL
    );
