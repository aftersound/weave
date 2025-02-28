package io.aftersound.func.common;

import io.aftersound.func.*;
import io.aftersound.util.TreeNode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class DateFuncFactory extends MasterAwareFuncFactory {

        private static final List<Descriptor> DESCRIPTORS = Arrays.asList(
//            Descriptor
//                    .builder(
//                            "DATE:FORMAT",
//                            "Date",
//                            "String"
//                    )
//                    .withDescription("Format ")
//                    .withControls(
//                            Control
//                                    .builder(
//                                            "String",
//                                            "Date format"
//                                    )
//                                    .build()
//                    )
//                    .withExamples(
//                            Example.as(
//                                    "DATE:FORMAT(yyyyHHdd)",
//                                    "Format input Date object in 'yyyyHHdd'"
//                            )
//                    )
//                    .build(),
//            Descriptor
//                    .builder(
//                            "DATE:FROM",
//                            "Input in 'Source Type or Source Format'",
//                            "Date"
//                    )
//                    .withDescription("Convert and create Date object from input in source type as specified in control parameter")
//                    .withControls(
//                            Control
//                                    .builder(
//                                            "String",
//                                            "Source Type or Source Format"
//                                    )
//                                    .build()
//                    )
//                    .withExamples(
//                            Example.as(
//                                    "DATE:FROM(yyyyHHdd)",
//                                    "Convert and create Date object from input string in 'yyyyHHdd' format"
//                            ),
//                            Example.as(
//                                    "DATE:FROM(Long)",
//                                    "Convert and create Date object from input Long value"
//                            ),
//                            Example.as(
//                                    "DATE:FROM(LONG)",
//                                    "Convert and create Date object from input literal representing long value"
//                            )
//                    )
//                    .build(),
//            Descriptor.builder("DATE:LIST:FROM", "TBD", "TBD")
//                    .build()
    );

    @Override
    public List<Descriptor> getFuncDescriptors() {
        return DESCRIPTORS;
    }

    @Override
    public <IN, OUT> Func<IN, OUT> create(TreeNode spec) {
        final String funcName = spec.getData();

        if ("DATE:FORMAT".equals(funcName)) {
            return createFormatDateFunc(spec);
        }

        if ("DATE:FROM".equals(funcName)) {
            return createDateFrom(spec);
        }

        if ("LIST<DATE>:FROM".equals(funcName)) {
            return createListFrom(spec);
        }

        return null;
    }

    static class FormatDateFunc extends AbstractFuncWithHints<Date, String> {

        private final String dateFormat;

        public FormatDateFunc(String dateFormat) {
            this.dateFormat = dateFormat;
        }

        @Override
        public String apply(Date source) {
            if (source != null) {
                SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
                return sdf.format(source);
            } else {
                return null;
            }
        }
    }

    static class DateFromLong extends AbstractFuncWithHints<Long, Date> {

        @Override
        public Date apply(Long source) {
            if (source != null) {
                return new Date(source);
            } else {
                return null;
            }
        }
    }

    static class DateFromStringLong extends AbstractFuncWithHints<String, Date> {

        @Override
        public Date apply(String source) {
            if (source != null) {
                return new Date(Long.parseLong(source));
            } else {
                return null;
            }
        }
    }

    static class DateFromDateFormat extends AbstractFuncWithHints<String, Date> {

        private final String dateFormat;

        protected DateFromDateFormat(String dateFormat) {
            this.dateFormat = dateFormat;
        }

        @Override
        public Date apply(String source) {
            if (source != null) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
                    return sdf.parse(source);
                } catch (ParseException e) {
                    throw new ExecutionException("failed to parse " + source + " into Date", e);
                }
            } else {
                return null;
            }
        }
    }

    static abstract class FromList<S> extends AbstractFuncWithHints<List<S>, List<Date>> {

        @Override
        public final List<Date> apply(List<S> source) {
            if (source == null) {
                return null;
            }

            List<Date> dates = new ArrayList<>(source.size());
            for (S e : source) {
                dates.add(toDate(e));
            }
            return dates;
        }

        protected abstract Date toDate(S e);
    }

    static class FromLongList extends FromList<Long> {

        @Override
        protected Date toDate(Long source) {
            if (source != null) {
                return new Date(source);
            } else {
                return null;
            }
        }

    }

    static class FromStringLongList extends FromList<String> {

        @Override
        protected Date toDate(String source) {
            if (source != null) {
                return new Date(Long.parseLong(source));
            } else {
                return null;
            }
        }

    }

    static class FromDateFormatList extends FromList<String> {

        private final String dateFormat;

        public FromDateFormatList(String dateFormat) {
            this.dateFormat = dateFormat;
        }

        @Override
        protected Date toDate(String source) {
            if (source != null) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
                    return sdf.parse(source);
                } catch (ParseException e) {
                    throw new ExecutionException("failed to parse " + source + " into Date", e);
                }
            } else {
                return null;
            }
        }

    }

    private Func createFormatDateFunc(TreeNode spec) {
        return new FormatDateFunc(spec.getDataOfChildAt(0));
    }

    private Func createDateFrom(TreeNode spec) {
        String sourceFormat = spec.getDataOfChildAt(0);
        if ("Long".equals(sourceFormat)) {
            return new DateFromLong();
        } else if ("LONG".equals(sourceFormat)) {
            return new DateFromStringLong();
        } else {
            return new DateFromDateFormat(sourceFormat);
        }
    }

    private Func createListFrom(TreeNode spec) {
        String sourceFormat = spec.getDataOfChildAt(0);
        if ("Long".equals(sourceFormat)) {
            return new FromLongList();
        } else if ("LONG".equals(sourceFormat)) {
            return new FromStringLongList();
        } else {
            return new FromDateFormatList(sourceFormat);
        }
    }

}
