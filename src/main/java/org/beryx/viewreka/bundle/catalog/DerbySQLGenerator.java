package org.beryx.viewreka.bundle.catalog;

import com.vaadin.data.util.sqlcontainer.query.generator.DefaultSQLGenerator;

public class DerbySQLGenerator extends DefaultSQLGenerator {

    public DerbySQLGenerator() {
    }

    /** Construct a DerbySQLGenerator with the specified identifiers for start and end of quoted strings. The identifiers
     * may be different depending on the database engine and it's settings.
     *
     * @param quoteStart the identifier (character) denoting the start of a quoted string
     * @param quoteEnd the identifier (character) denoting the end of a quoted string */
    public DerbySQLGenerator(String quoteStart, String quoteEnd) {
        super(quoteStart, quoteEnd);
    }

    /** Generates the LIMIT and OFFSET clause.
     *
     * @param sb StringBuffer to which the clause is appended.
     * @param offset Value for offset.
     * @param pagelength Value for pagelength.
     * @return StringBuffer with LIMIT and OFFSET clause added. */
    protected StringBuffer generateLimits(StringBuffer sb, int offset, int pagelength) {
        sb.append(" OFFSET ").append(offset).append(" ROWS").append(" FETCH NEXT ").append(pagelength).append(" ROWS ONLY");
        return sb;
    }
}
