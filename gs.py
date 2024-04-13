from pathlib import Path

import gspread
from gspread.utils import ExportFormat

DOCUMENT_URL = '' # подставить урл до документа
# 'https://docs.google.com/spreadsheets/d/17So9e96QM-xxxxxxxxxxxxxxxxx/'

def main():
    # Положить креды в ~/.config/gspread/service_account.json
    # https://docs.gspread.org/en/latest/oauth2.html#for-bots-using-service-account
    gc = gspread.service_account()
    sheet = gc.open_by_url(DOCUMENT_URL)

    content: bytes = sheet.export(format=ExportFormat.EXCEL)

    Path('content/quiz.xlsx').write_bytes(content)


if __name__ == '__main__':
    main()
