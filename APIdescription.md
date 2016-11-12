#1. Создание аккаунта:

###request
curl -X POST --header 'Content-Type: application/json' --header 'Accept: application/json' 'http://88.198.13.202:6869/addresses'

###response
{
  "address": "3NCH9Hk7Cy4vuJHG1MK3hXbhgdpcwN9vU77"
}

#2. Перевод денег (Waves)

###request
curl -X POST --header 'Content-Type: application/json' --header 'Accept: application/json' -d '{ \ 
    "sender": "3N9R13Htt2PKHSace2V6be1DYEjaSKWATLc", \ 
    "recipient": "3N8YmBwT2tTyMNEfyAiyvZtvT99dVmSFRWd", \ 
    "amount": 101000000, \ 
    "feeAmount": 100000, \ 
    "attachment": "base" \ 
  }' 'http://88.198.13.202:6869/assets/transfer'

###response

{
  "type": 4,
  "id": "7SDSHSXuf4jx9JT1K1UgpRmkYY7ipgeV6SnGBQXS3bgt",
  "sender": "3N9R13Htt2PKHSace2V6be1DYEjaSKWATLc",
  "senderPublicKey": "EzfL6UatJGvU7bX12aZZUUgCjNgjKdnUGmoFvgbWFCEv",
  "recipient": "3N8YmBwT2tTyMNEfyAiyvZtvT99dVmSFRWd",
  "assetId": null,
  "amount": 101000000,
  "feeAsset": null,
  "fee": 100000,
  "timestamp": 1478961509075,
  "attachment": "base",
  "signature": "28a5JjeXiL1uN2F4eP36vR6Hi3yWTZMAW8bfwkxNT4guk63sn8GisZ1jUBaQbpYDkKLMttCANDHSUyb56vmuXzDT"
}

#3. Проверка баланса
###request
curl -X GET --header 'Accept: application/json' 'http://88.198.13.202:6869/addresses/balance/3N8YmBwT2tTyMNEfyAiyvZtvT99dVmSFRWd'

###response

{
  "address": "3N8YmBwT2tTyMNEfyAiyvZtvT99dVmSFRWd",
  "confirmations": 0,
  "balance": 101000000
}

#4. Выпуск ассета

###request

curl -X POST --header 'Content-Type: application/json' --header 'Accept: application/json' -d '{ \ 
   "name": "MyAsset1", \ 
   "quantity": 1000000, \ 
   "description": "string", \ 
   "sender": "3N8YmBwT2tTyMNEfyAiyvZtvT99dVmSFRWd", \ 
   "decimals": 0, \ 
   "reissuable": false, \ 
   "fee": 100000000 \ 
 } \ 
 ' 'http://88.198.13.202:6869/assets/issue'

###response

{
  "type": 3,
  "id": "BJJWPgqaSCd6SK8huDKHC7tuH6vwtezKCQjfGkZxreYP",
  "sender": "3N8YmBwT2tTyMNEfyAiyvZtvT99dVmSFRWd",
  "senderPublicKey": "6bCWffMrLLV2QfBqi3qPQuk6Voy2eBUCD5RVMjsSKn3b",
  "assetId": "BJJWPgqaSCd6SK8huDKHC7tuH6vwtezKCQjfGkZxreYP",
  "name": "DxbJoqsBnmv",
  "description": "zVbyBrMk",
  "quantity": 1000000,
  "decimals": 0,
  "reissuable": false,
  "fee": 100000000,
  "timestamp": 1478961613055,
  "signature": "Z9RrUoBteKRCTGt5sxrDopy4iS1pzA3RFaduu5df1nXVLAFaaDyNXB6TrzxCKJYpR4NgayagTxUbuRU1L3ZeVwK"
}
