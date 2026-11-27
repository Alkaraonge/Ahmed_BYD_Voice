#!/usr/bin/env bash
# ============================================================
# توليد مفتاح توقيع دائم لإصدارات أحمد للبرمجة (مرة وحدة بس)
# الناتج: أربع قيم تلصقهن بـ GitHub Secrets
# ------------------------------------------------------------
# الاستخدام:  bash setup_keystore.sh
# ============================================================
set -euo pipefail

KEYSTORE_FILE="release-byd.jks"
KEY_ALIAS="byd"

echo "== [1/3] توليد المفتاح (احفظ كلمة السر اللي تدخلها — رح تحتاجها مرتين) =="
if [ -f "$KEYSTORE_FILE" ]; then
    echo "!! $KEYSTORE_FILE موجود مسبقاً — ما راح أستبدله. احذفه يدوياً إذا تريد من جديد."
else
    keytool -genkeypair -v \
        -keystore "$KEYSTORE_FILE" \
        -alias "$KEY_ALIAS" \
        -keyalg RSA -keysize 2048 -validity 10950
fi

echo
echo "== [2/3] KEYSTORE_BASE64 — انسخ كل الناتج الطويل هذا السطر: =="
base64 -w0 "$KEYSTORE_FILE"; echo
echo
echo "== [3/3] القيم الأربعة لـ GitHub Secrets (Settings ← Secrets ← Actions): =="
echo "  KEYSTORE_BASE64     = الناتج الطويل اللي فوق ☝️"
echo "  KEYSTORE_PASSWORD   = كلمة سر المفتاح"
echo "  KEY_ALIAS           = $KEY_ALIAS"
echo "  KEY_PASSWORD        = كلمة سر المفتاح (نفسها)"
echo
echo "ملف $KEYSTORE_FILE ابقه بحفظ — أي فقدان يعني مستحيل توقع تحديثات فوق نفس التوقيع."
